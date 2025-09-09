import React, { useState, useRef, useEffect } from 'react';
import {
  Box,
  VStack,
  HStack,
  Button,
  Text,
  useColorModeValue,
  Icon,
  Tooltip,
  Divider,
  Badge,
  useToast,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
  Textarea,
  useDisclosure,
} from '@chakra-ui/react';
import {
  FiBold,
  FiItalic,
  FiUnderline,
  FiList,
  FiAlignLeft,
  FiAlignCenter,
  FiAlignRight,
  FiLink,
  FiImage,
  FiCode,
  FiEye,
  FiEyeOff,
  FiSave,
  FiType,
  FiTable,
  FiMaximize2,
  FiMinimize2,
} from 'react-icons/fi';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import htmlReactParser from 'html-react-parser';

interface RichTextEditorProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  height?: string;
  readOnly?: boolean;
  showToolbar?: boolean;
  showHtmlMode?: boolean;
  showPreview?: boolean;
  onSave?: () => void;
  autoSave?: boolean;
  autoSaveInterval?: number;
}

interface EmailBlock {
  id: string;
  type: 'header' | 'footer' | 'image-text' | 'cta-button' | 'social-links' | 'spacer';
  content: string;
  styles?: string;
}

const RichTextEditor: React.FC<RichTextEditorProps> = ({
  value,
  onChange,
  placeholder = "Start writing your email...",
  height = "400px",
  readOnly = false,
  showToolbar = true,
  showHtmlMode = true,
  showPreview = true,
  onSave,
  autoSave = false,
  autoSaveInterval = 30000, // 30 seconds
}) => {
  const [isHtmlMode, setIsHtmlMode] = useState(false);
  const [isPreviewMode, setIsPreviewMode] = useState(false);
  const [htmlContent, setHtmlContent] = useState(value);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);
  
  const quillRef = useRef<ReactQuill>(null);
  const toast = useToast();
  const { isOpen: isHtmlModalOpen, onOpen: onHtmlModalOpen, onClose: onHtmlModalClose } = useDisclosure();
  
  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.600');
  const textColor = useColorModeValue('gray.800', 'white');
  const blocksBgColor = useColorModeValue('gray.50', 'gray.700');

  // Auto-save functionality
  useEffect(() => {
    if (autoSave && onSave) {
      const interval = setInterval(() => {
        if (hasUnsavedChanges) {
          onSave();
          setHasUnsavedChanges(false);
        }
      }, autoSaveInterval);
      
      return () => clearInterval(interval);
    }
  }, [autoSave, onSave, hasUnsavedChanges, autoSaveInterval]);

  // Sync HTML content with value
  useEffect(() => {
    if (isHtmlMode) {
      setHtmlContent(value);
    }
  }, [value, isHtmlMode]);

  const handleContentChange = (content: string) => {
    onChange(content);
    setHasUnsavedChanges(true);
  };

  const handleHtmlChange = (content: string) => {
    setHtmlContent(content);
    onChange(content);
    setHasUnsavedChanges(true);
  };

  const handleSave = () => {
    if (onSave) {
      onSave();
      setHasUnsavedChanges(false);
      toast({
        title: 'Saved',
        description: 'Template saved successfully',
        status: 'success',
        duration: 2000,
        isClosable: true,
      });
    }
  };

  const handleHtmlModeToggle = () => {
    if (isHtmlMode) {
      // Switching from HTML to WYSIWYG
      onChange(htmlContent);
    } else {
      // Switching from WYSIWYG to HTML
      setHtmlContent(value);
    }
    setIsHtmlMode(!isHtmlMode);
  };

  const handlePreviewToggle = () => {
    setIsPreviewMode(!isPreviewMode);
  };

  const handleFullscreenToggle = () => {
    setIsFullscreen(!isFullscreen);
  };

  // Quill editor configuration
  const quillModules = {
    toolbar: {
      container: [
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
        [{ 'align': [] }],
        ['link', 'image'],
        ['blockquote', 'code-block'],
        ['clean']
      ],
    },
    clipboard: {
      matchVisual: false,
    }
  };

  const quillFormats = [
    'header', 'bold', 'italic', 'underline', 'strike',
    'color', 'background', 'list', 'bullet', 'align',
    'link', 'image', 'blockquote', 'code-block'
  ];

  // Email blocks for drag and drop
  const emailBlocks: EmailBlock[] = [
    {
      id: 'header',
      type: 'header',
      content: '<div style="text-align: center; padding: 20px; background-color: #f8f9fa;"><h1>Your Header</h1></div>',
      styles: 'background-color: #f8f9fa; padding: 20px; text-align: center;'
    },
    {
      id: 'footer',
      type: 'footer',
      content: '<div style="text-align: center; padding: 20px; background-color: #f8f9fa; font-size: 12px; color: #666;">© 2024 Your Company. All rights reserved.</div>',
      styles: 'background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 12px; color: #666;'
    },
    {
      id: 'image-text',
      type: 'image-text',
      content: '<div style="display: flex; align-items: center; padding: 20px;"><div style="flex: 1; padding-right: 20px;"><h3>Your Title</h3><p>Your content goes here...</p></div><div style="flex: 1;"><img src="https://via.placeholder.com/300x200" alt="Image" style="width: 100%; height: auto;"></div></div>',
      styles: 'display: flex; align-items: center; padding: 20px;'
    },
    {
      id: 'cta-button',
      type: 'cta-button',
      content: '<div style="text-align: center; padding: 20px;"><a href="#" style="background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Call to Action</a></div>',
      styles: 'text-align: center; padding: 20px;'
    },
    {
      id: 'social-links',
      type: 'social-links',
      content: '<div style="text-align: center; padding: 20px;"><a href="#" style="margin: 0 10px; color: #007bff;">Facebook</a><a href="#" style="margin: 0 10px; color: #007bff;">Twitter</a><a href="#" style="margin: 0 10px; color: #007bff;">LinkedIn</a></div>',
      styles: 'text-align: center; padding: 20px;'
    },
    {
      id: 'spacer',
      type: 'spacer',
      content: '<div style="height: 40px;"></div>',
      styles: 'height: 40px;'
    }
  ];

  const insertBlock = (block: EmailBlock) => {
    if (quillRef.current) {
      const quill = quillRef.current.getEditor();
      const range = quill.getSelection();
      const index = range ? range.index : quill.getLength();
      
      quill.clipboard.dangerouslyPasteHTML(index, block.content);
      setHasUnsavedChanges(true);
    }
  };

  const renderToolbar = () => (
    <HStack spacing={2} p={2} bg={bgColor} borderBottom="1px" borderColor={borderColor} wrap="wrap">
      {/* Formatting Tools */}
      <HStack spacing={1}>
        <Tooltip label="Bold">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm" fontWeight="bold">B</Text>
          </Button>
        </Tooltip>
        <Tooltip label="Italic">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm" fontStyle="italic">I</Text>
          </Button>
        </Tooltip>
        <Tooltip label="Underline">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm" textDecoration="underline">U</Text>
          </Button>
        </Tooltip>
        <Tooltip label="Strikethrough">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm" fontWeight="bold">S</Text>
          </Button>
        </Tooltip>
      </HStack>

      <Divider orientation="vertical" height="20px" />

      {/* Alignment Tools */}
      <HStack spacing={1}>
        <Tooltip label="Align Left">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm">←</Text>
          </Button>
        </Tooltip>
        <Tooltip label="Align Center">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm">↔</Text>
          </Button>
        </Tooltip>
        <Tooltip label="Align Right">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm">→</Text>
          </Button>
        </Tooltip>
      </HStack>

      <Divider orientation="vertical" height="20px" />

      {/* Content Tools */}
      <HStack spacing={1}>
        <Tooltip label="Insert Link">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm">🔗</Text>
          </Button>
        </Tooltip>
        <Tooltip label="Insert Image">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm">🖼</Text>
          </Button>
        </Tooltip>
        <Tooltip label="Insert Table">
          <Button size="sm" variant="ghost">
            <Text fontSize="sm">⊞</Text>
          </Button>
        </Tooltip>
      </HStack>

      <Divider orientation="vertical" height="20px" />

      {/* Mode Toggles */}
      <HStack spacing={1}>
        {showHtmlMode && (
          <Tooltip label={isHtmlMode ? "Switch to WYSIWYG" : "Switch to HTML"}>
            <Button
              size="sm"
              variant={isHtmlMode ? "solid" : "ghost"}
              colorScheme={isHtmlMode ? "blue" : "gray"}
              onClick={handleHtmlModeToggle}
            >
              <Text fontSize="sm">&lt;/&gt;</Text>
            </Button>
          </Tooltip>
        )}
        
        {showPreview && (
          <Tooltip label={isPreviewMode ? "Hide Preview" : "Show Preview"}>
            <Button
              size="sm"
              variant={isPreviewMode ? "solid" : "ghost"}
              colorScheme={isPreviewMode ? "green" : "gray"}
              onClick={handlePreviewToggle}
            >
              <Text fontSize="sm">{isPreviewMode ? "👁‍🗨" : "👁"}</Text>
            </Button>
          </Tooltip>
        )}
      </HStack>

      <Divider orientation="vertical" height="20px" />

      {/* Action Buttons */}
      <HStack spacing={1}>
        <Tooltip label="Save">
          <Button
            size="sm"
            variant="ghost"
            onClick={handleSave}
            isDisabled={!hasUnsavedChanges}
          >
            <Text fontSize="sm">💾</Text>
          </Button>
        </Tooltip>
        
        <Tooltip label={isFullscreen ? "Exit Fullscreen" : "Fullscreen"}>
          <Button 
            size="sm" 
            variant="ghost"
            onClick={handleFullscreenToggle}
          >
            <Text fontSize="sm">{isFullscreen ? "⤓" : "⤢"}</Text>
          </Button>
        </Tooltip>
      </HStack>

      {/* Status Indicators */}
      <HStack spacing={2} ml="auto">
        {hasUnsavedChanges && (
          <Badge colorScheme="orange" variant="subtle">
            Unsaved
          </Badge>
        )}
        {autoSave && (
          <Badge colorScheme="green" variant="subtle">
            Auto-save
          </Badge>
        )}
      </HStack>
    </HStack>
  );

  const renderEmailBlocks = () => (
    <Box p={4} bg={blocksBgColor} borderRight="1px" borderColor={borderColor}>
      <Text fontSize="sm" fontWeight="bold" mb={3} color={textColor}>
        Email Blocks
      </Text>
      <VStack spacing={2} align="stretch">
        {emailBlocks.map((block) => (
          <Button
            key={block.id}
            size="sm"
            variant="outline"
            onClick={() => insertBlock(block)}
            justifyContent="flex-start"
            textTransform="capitalize"
          >
            {block.type.replace('-', ' ')}
          </Button>
        ))}
      </VStack>
    </Box>
  );

  const renderEditor = () => {
    if (isPreviewMode) {
      return (
        <Box p={4} bg={bgColor} height={height} overflow="auto">
          <Text fontSize="sm" color="gray.500" mb={2}>Preview:</Text>
          <Box border="1px" borderColor={borderColor} p={4} borderRadius="md">
            {htmlReactParser(value)}
          </Box>
        </Box>
      );
    }

    if (isHtmlMode) {
      return (
        <Box p={4} bg={bgColor} height={height}>
          <HStack justify="space-between" mb={2}>
            <Text fontSize="sm" color="gray.500">HTML Source:</Text>
            <Button size="xs" onClick={onHtmlModalOpen}>
              <Text fontSize="xs">⤢</Text>
            </Button>
          </HStack>
          <Textarea
            value={htmlContent}
            onChange={(e) => handleHtmlChange(e.target.value)}
            placeholder="Enter HTML content..."
            height="calc(100% - 40px)"
            fontFamily="monospace"
            fontSize="sm"
            resize="none"
          />
        </Box>
      );
    }

    return (
      <Box bg={bgColor} height={height}>
        <ReactQuill
          ref={quillRef}
          theme="snow"
          value={value}
          onChange={handleContentChange}
          placeholder={placeholder}
          readOnly={readOnly}
          modules={quillModules}
          formats={quillFormats}
          style={{ height: 'calc(100% - 42px)' }}
        />
      </Box>
    );
  };

  return (
    <Box
      border="1px"
      borderColor={borderColor}
      borderRadius="md"
      overflow="hidden"
      bg={bgColor}
      width={isFullscreen ? "100vw" : "100%"}
      height={isFullscreen ? "100vh" : "auto"}
      position={isFullscreen ? "fixed" : "relative"}
      top={isFullscreen ? 0 : "auto"}
      left={isFullscreen ? 0 : "auto"}
      zIndex={isFullscreen ? 9999 : "auto"}
    >
      {showToolbar && renderToolbar()}
      
      <HStack align="stretch" height={height}>
        {!isPreviewMode && renderEmailBlocks()}
        <Box flex={1}>
          {renderEditor()}
        </Box>
      </HStack>

      {/* HTML Modal */}
      <Modal isOpen={isHtmlModalOpen} onClose={onHtmlModalClose} size="xl">
        <ModalOverlay />
        <ModalContent maxW="90vw" maxH="90vh">
          <ModalHeader>HTML Source Editor</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <Textarea
              value={htmlContent}
              onChange={(e) => setHtmlContent(e.target.value)}
              placeholder="Enter HTML content..."
              height="60vh"
              fontFamily="monospace"
              fontSize="sm"
            />
            <HStack mt={4} justify="flex-end">
              <Button variant="outline" onClick={onHtmlModalClose}>
                Cancel
              </Button>
              <Button 
                colorScheme="blue" 
                onClick={() => {
                  handleHtmlChange(htmlContent);
                  onHtmlModalClose();
                }}
              >
                Apply Changes
              </Button>
            </HStack>
          </ModalBody>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default RichTextEditor;
