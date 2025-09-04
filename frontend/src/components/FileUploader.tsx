import React from 'react';
import { useDropzone, FileRejection } from 'react-dropzone';
import {
  Box,
  Text,
  VStack,
  HStack,
  Icon,
  Button,
  List,
  ListItem,
  IconButton,
} from '@chakra-ui/react';
import { IconType } from 'react-icons';
import { MdCloudUpload, MdDelete } from 'react-icons/md';

interface FileUploaderProps {
  files: File[];
  onFilesChange: (files: File[]) => void;
}

const FileUploader: React.FC<FileUploaderProps> = ({ files, onFilesChange }) => {
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: (acceptedFiles: File[]) => {
      onFilesChange([...files, ...acceptedFiles]);
    },
    accept: {
      'image/*': ['.png', '.jpg', '.jpeg', '.gif'],
      'video/*': ['.mp4', '.webm'],
      'audio/*': ['.mp3', '.wav'],
      'application/pdf': ['.pdf'],
    },
  });

  const removeFile = (index: number) => {
    const newFiles = [...files];
    newFiles.splice(index, 1);
    onFilesChange(newFiles);
  };

  return (
    <VStack spacing={4} width="100%">
      <Box
        {...getRootProps()}
        width="100%"
        p={6}
        borderWidth={2}
        borderRadius="lg"
        borderStyle="dashed"
        borderColor={isDragActive ? 'blue.500' : 'gray.300'}
        bg={isDragActive ? 'blue.50' : 'gray.50'}
        cursor="pointer"
        transition="all 0.2s"
        _hover={{
          borderColor: 'blue.500',
          bg: 'blue.50',
        }}
      >
        <input {...getInputProps()} />
        <VStack spacing={2}>
          <Icon 
            as={MdCloudUpload as any} 
            fontSize="32px" 
            color={isDragActive ? 'blue.500' : 'gray.500'} 
          />
          <Text color={isDragActive ? 'blue.500' : 'gray.500'} textAlign="center">
            {isDragActive
              ? 'Drop the files here...'
              : 'Drag and drop files here, or click to select files'}
          </Text>
          <Text fontSize="sm" color="gray.500">
            Supported: Images, Videos, Audio, PDF
          </Text>
        </VStack>
      </Box>

      {files.length > 0 && (
        <List spacing={2} width="100%">
          {files.map((file, index) => (
            <ListItem
              key={index}
              p={2}
              bg="white"
              borderRadius="md"
              shadow="sm"
              borderWidth={1}
            >
              <HStack justify="space-between">
                <Text fontSize="sm" noOfLines={1}>
                  {file.name}
                </Text>
                <IconButton
                  aria-label="Remove file"
                  icon={<Icon as={MdDelete as any} fontSize="20px" />}
                  size="sm"
                  variant="ghost"
                  colorScheme="red"
                  onClick={() => removeFile(index)}
                />
              </HStack>
            </ListItem>
          ))}
        </List>
      )}
    </VStack>
  );
};

export default FileUploader; 