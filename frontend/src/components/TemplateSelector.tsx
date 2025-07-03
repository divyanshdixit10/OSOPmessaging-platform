import React, { useState } from 'react';
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  Input,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Select,
  useDisclosure,
  VStack,
  useToast
} from '@chakra-ui/react';
import { EmailTemplate } from '../types/EmailTemplate';
import { EmailEditor } from './EmailEditor';

interface TemplateSelectorProps {
  templates: EmailTemplate[];
  selectedTemplate?: EmailTemplate;
  onTemplateSelect: (template: EmailTemplate) => void;
  onTemplateSave: (template: EmailTemplate) => Promise<EmailTemplate>;
}

export const TemplateSelector: React.FC<TemplateSelectorProps> = ({
  templates,
  selectedTemplate,
  onTemplateSelect,
  onTemplateSave
}) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const toast = useToast();
  const [newTemplate, setNewTemplate] = useState<EmailTemplate>({
    name: '',
    subject: '',
    body: ''
  });

  const handleCreateTemplate = async () => {
    try {
      if (!newTemplate.name || !newTemplate.subject || !newTemplate.body) {
        toast({
          title: 'Validation Error',
          description: 'Please fill in all required fields',
          status: 'error',
          duration: 3000
        });
        return;
      }

      const savedTemplate = await onTemplateSave(newTemplate);
      toast({
        title: 'Success',
        description: 'Template created successfully',
        status: 'success',
        duration: 3000
      });
      onClose();
      setNewTemplate({ name: '', subject: '', body: '' });
      onTemplateSelect(savedTemplate);
    } catch (error) {
      toast({
        title: 'Error',
        description: error instanceof Error ? error.message : 'Failed to create template',
        status: 'error',
        duration: 5000
      });
    }
  };

  return (
    <Box>
      <FormControl>
        <FormLabel>Select Template</FormLabel>
        <Select
          value={selectedTemplate?.id || ''}
          onChange={(e) => {
            const template = templates.find(t => t.id === e.target.value);
            if (template) onTemplateSelect(template);
          }}
          placeholder="Choose a template"
        >
          {templates.map((template) => (
            <option key={template.id} value={template.id}>
              {template.name}
            </option>
          ))}
        </Select>
      </FormControl>

      <Button mt={4} onClick={onOpen} colorScheme="blue" variant="outline">
        Create New Template
      </Button>

      <Modal isOpen={isOpen} onClose={onClose} size="xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Create New Template</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack spacing={4}>
              <FormControl isRequired>
                <FormLabel>Template Name</FormLabel>
                <Input
                  value={newTemplate.name}
                  onChange={(e) => setNewTemplate({ ...newTemplate, name: e.target.value })}
                  placeholder="Enter template name"
                />
              </FormControl>

              <EmailEditor
                subject={newTemplate.subject}
                body={newTemplate.body}
                onSubjectChange={(subject) => setNewTemplate({ ...newTemplate, subject })}
                onBodyChange={(body) => setNewTemplate({ ...newTemplate, body })}
              />
            </VStack>
          </ModalBody>

          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={onClose}>
              Cancel
            </Button>
            <Button colorScheme="blue" onClick={handleCreateTemplate}>
              Save Template
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Box>
  );
}; 