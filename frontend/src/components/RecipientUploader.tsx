import React, { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  HStack,
  Input,
  List,
  ListItem,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalHeader,
  ModalOverlay,
  Select,
  Tag,
  TagCloseButton,
  TagLabel,
  Text,
  VStack,
  useDisclosure,
  useToast
} from '@chakra-ui/react';
import { Recipient } from '../types/EmailRequest';
import { parseCSV, parseExcel, getFileColumns } from '../utils/fileParser';
import { validateEmail, extractEmailsFromText, removeDuplicateEmails } from '../utils/emailValidator';

interface RecipientUploaderProps {
  recipients: Recipient[];
  onRecipientsChange: (recipients: Recipient[]) => void;
}

export const RecipientUploader: React.FC<RecipientUploaderProps> = ({
  recipients,
  onRecipientsChange
}) => {
  const toast = useToast();
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [manualInput, setManualInput] = useState('');
  const [columns, setColumns] = useState<string[]>([]);
  const [selectedEmailColumn, setSelectedEmailColumn] = useState('');
  const [selectedNameColumn, setSelectedNameColumn] = useState('');

  const onDrop = useCallback(async (acceptedFiles: File[]) => {
    try {
      const file = acceptedFiles[0];
      if (!file) return;

      // Get columns for mapping
      const fileColumns = await getFileColumns(file);
      setColumns(fileColumns);

      if (fileColumns.length > 0) {
        // Try to automatically detect email and name columns
        const emailCol = fileColumns.find(col => 
          col.toLowerCase().includes('email')) || fileColumns[0];
        const nameCol = fileColumns.find(col => 
          col.toLowerCase().includes('name'));

        setSelectedEmailColumn(emailCol);
        if (nameCol) setSelectedNameColumn(nameCol);
        onOpen();
      }

      // Parse file based on type
      let newRecipients: Recipient[] = [];
      if (file.name.endsWith('.csv')) {
        newRecipients = await parseCSV(file, {
          emailColumn: selectedEmailColumn,
          nameColumn: selectedNameColumn
        });
      } else if (file.name.match(/\.xlsx?$/)) {
        newRecipients = await parseExcel(file, {
          emailColumn: selectedEmailColumn,
          nameColumn: selectedNameColumn
        });
      } else if (file.name.endsWith('.txt')) {
        const text = await file.text();
        newRecipients = extractEmailsFromText(text);
      }

      const allRecipients = removeDuplicateEmails([...recipients, ...newRecipients]);
      onRecipientsChange(allRecipients);

      toast({
        title: 'Recipients added',
        description: `Added ${newRecipients.length} recipients from file`,
        status: 'success',
        duration: 3000,
      });
    } catch (error) {
      toast({
        title: 'Error uploading file',
        description: error instanceof Error ? error.message : 'Unknown error occurred',
        status: 'error',
        duration: 5000,
      });
    }
  }, [recipients, onRecipientsChange, selectedEmailColumn, selectedNameColumn, toast]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'text/csv': ['.csv'],
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': ['.xlsx'],
      'application/vnd.ms-excel': ['.xls'],
      'text/plain': ['.txt']
    },
    multiple: false
  });

  const handleManualAdd = () => {
    if (!manualInput.trim()) return;

    const newRecipients = extractEmailsFromText(manualInput);
    const allRecipients = removeDuplicateEmails([...recipients, ...newRecipients]);
    onRecipientsChange(allRecipients);
    setManualInput('');
  };

  const handleRemoveRecipient = (email: string) => {
    onRecipientsChange(recipients.filter(r => r.email !== email));
  };

  const handleColumnMapping = () => {
    // Re-trigger file processing with selected columns
    onClose();
  };

  return (
    <VStack spacing={4} align="stretch" w="100%">
      <Box
        {...getRootProps()}
        p={6}
        border="2px dashed"
        borderColor={isDragActive ? 'blue.400' : 'gray.200'}
        borderRadius="md"
        textAlign="center"
        cursor="pointer"
      >
        <input {...getInputProps()} />
        <Text>
          {isDragActive
            ? 'Drop the file here...'
            : 'Drag and drop a file here, or click to select'}
        </Text>
        <Text fontSize="sm" color="gray.500" mt={2}>
          Supported formats: CSV, Excel, TXT
        </Text>
      </Box>

      <FormControl>
        <FormLabel>Add Recipients Manually</FormLabel>
        <HStack>
          <Input
            value={manualInput}
            onChange={(e) => setManualInput(e.target.value)}
            placeholder="Enter emails (comma or newline separated)"
          />
          <Button onClick={handleManualAdd}>Add</Button>
        </HStack>
      </FormControl>

      <Box>
        <Text mb={2}>Recipients ({recipients.length})</Text>
        <List spacing={2}>
          {recipients.map((recipient) => (
            <ListItem key={recipient.email}>
              <Tag
                size="md"
                colorScheme={recipient.isValid ? 'green' : 'red'}
                borderRadius="full"
              >
                <TagLabel>{recipient.email}</TagLabel>
                <TagCloseButton onClick={() => handleRemoveRecipient(recipient.email)} />
              </Tag>
            </ListItem>
          ))}
        </List>
      </Box>

      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Map Columns</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack spacing={4} pb={4}>
              <FormControl>
                <FormLabel>Email Column</FormLabel>
                <Select
                  value={selectedEmailColumn}
                  onChange={(e) => setSelectedEmailColumn(e.target.value)}
                >
                  {columns.map(col => (
                    <option key={col} value={col}>{col}</option>
                  ))}
                </Select>
              </FormControl>

              <FormControl>
                <FormLabel>Name Column (Optional)</FormLabel>
                <Select
                  value={selectedNameColumn}
                  onChange={(e) => setSelectedNameColumn(e.target.value)}
                >
                  <option value="">None</option>
                  {columns.map(col => (
                    <option key={col} value={col}>{col}</option>
                  ))}
                </Select>
              </FormControl>

              <Button onClick={handleColumnMapping} colorScheme="blue">
                Import Recipients
              </Button>
            </VStack>
          </ModalBody>
        </ModalContent>
      </Modal>
    </VStack>
  );
}; 