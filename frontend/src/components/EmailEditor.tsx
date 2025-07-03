import React from 'react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import './EmailEditor.css';
import {
  Box,
  FormControl,
  FormLabel,
  Input,
  VStack,
  Text,
  useColorModeValue
} from '@chakra-ui/react';

interface EmailEditorProps {
  subject: string;
  body: string;
  onSubjectChange: (subject: string) => void;
  onBodyChange: (body: string) => void;
  isPreview?: boolean;
}

export const EmailEditor: React.FC<EmailEditorProps> = ({
  subject,
  body,
  onSubjectChange,
  onBodyChange,
  isPreview = false
}) => {
  const bgColor = useColorModeValue('white', 'gray.700');
  const borderColor = useColorModeValue('gray.200', 'gray.600');

  // Quill modules configuration
  const modules = {
    toolbar: [
      [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ 'list': 'ordered'}, { 'list': 'bullet' }],
      [{ 'align': [] }],
      ['link', 'image'],
      [{ 'color': [] }, { 'background': [] }],
      ['clean']
    ]
  };

  // Quill formats configuration
  const formats = [
    'header',
    'bold', 'italic', 'underline', 'strike',
    'list', 'bullet',
    'align',
    'link', 'image',
    'color', 'background'
  ];

  return (
    <VStack spacing={4} align="stretch" w="100%">
      <FormControl>
        <FormLabel>Subject</FormLabel>
        <Input
          value={subject}
          onChange={(e) => onSubjectChange(e.target.value)}
          placeholder="Enter email subject"
          isReadOnly={isPreview}
        />
      </FormControl>

      <FormControl>
        <FormLabel>Message</FormLabel>
        <Box
          borderWidth={1}
          borderColor={borderColor}
          borderRadius="md"
          bg={bgColor}
          className="quill-container"
        >
          {isPreview ? (
            <Box
              p={4}
              dangerouslySetInnerHTML={{ __html: body }}
              className="ql-editor"
            />
          ) : (
            <ReactQuill
              value={body}
              onChange={onBodyChange}
              modules={modules}
              formats={formats}
              placeholder="Enter your message here..."
              theme="snow"
            />
          )}
        </Box>
      </FormControl>

      {isPreview && (
        <Text fontSize="sm" color="gray.500">
          Preview mode - content cannot be edited
        </Text>
      )}
    </VStack>
  );
}; 