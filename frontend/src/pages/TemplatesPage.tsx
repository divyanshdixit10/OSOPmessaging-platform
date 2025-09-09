import React, { useState, useEffect } from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Button,
  Input,
  InputGroup,
  InputLeftElement,
  Grid,
  GridItem,
  Card,
  CardBody,
  CardHeader,
  Heading,
  Badge,
  IconButton,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  MenuDivider,
  useDisclosure,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  FormControl,
  FormLabel,
  Textarea,
  useToast,
  Avatar,
  Flex,
  Spacer,
  Divider,
  Tag,
  TagLabel,
  TagCloseButton,
  Icon,
} from '@chakra-ui/react';
import { IconType } from 'react-icons';
import {
  FiPlus,
  FiSearch,
  FiEdit3,
  FiTrash2,
  FiCopy,
  FiMoreVertical,
  FiMail,
  FiCalendar,
  FiUser,
  FiTag,
} from 'react-icons/fi';
import { EmailTemplate, EmailTemplateFormData } from '../types/EmailTemplate';
import TemplateService, { TemplateFilters } from '../api/templateService';

interface TemplateCardProps {
  template: EmailTemplate;
  onEdit: (template: EmailTemplate) => void;
  onDelete: (template: EmailTemplate) => void;
  onDuplicate: (template: EmailTemplate) => void;
}

const TemplateCard: React.FC<TemplateCardProps> = ({
  template,
  onEdit,
  onDelete,
  onDuplicate,
}) => {
  const [isHovered, setIsHovered] = useState(false);

  const getCategoryColor = (category: string) => {
    const colors: { [key: string]: string } = {
      newsletter: 'blue',
      welcome: 'green',
      promotional: 'orange',
      transactional: 'purple',
      default: 'gray',
    };
    return colors[category] || colors.default;
  };

  return (
    <Card
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      transition="all 0.2s"
      _hover={{ transform: 'translateY(-2px)', boxShadow: 'lg' }}
      cursor="pointer"
    >
      <CardHeader pb={2}>
        <HStack justify="space-between" align="flex-start">
          <VStack align="flex-start" spacing={2} flex={1}>
            <Heading size="md" color="gray.800" noOfLines={1}>
              {template.name || 'Untitled Template'}
            </Heading>
            <Text fontSize="sm" color="gray.600" noOfLines={2}>
              {template.subject}
            </Text>
            <HStack spacing={2}>
              <Badge colorScheme={getCategoryColor(template.category || 'default')} size="sm">
                {template.category || 'General'}
              </Badge>
              <Badge colorScheme="gray" size="sm">
                {template.type || 'Email'}
              </Badge>
            </HStack>
          </VStack>
          
          <Menu>
            <MenuButton
              as={IconButton}
                              icon={<Icon as={FiMoreVertical as any} />}
              variant="ghost"
              size="sm"
              aria-label="Template options"
              opacity={isHovered ? 1 : 0}
              transition="opacity 0.2s"
            />
            <MenuList>
                              <MenuItem icon={<Icon as={FiEdit3 as any} />} onClick={() => onEdit(template)}>
                Edit Template
              </MenuItem>
                              <MenuItem icon={<Icon as={FiCopy as any} />} onClick={() => onDuplicate(template)}>
                Duplicate
              </MenuItem>
              <MenuDivider />
                              <MenuItem icon={<Icon as={FiTrash2 as any} />} onClick={() => onDelete(template)} color="red.500">
                Delete
              </MenuItem>
            </MenuList>
          </Menu>
        </HStack>
      </CardHeader>

      <CardBody pt={0}>
        <Text fontSize="sm" color="gray.600" noOfLines={3} mb={4}>
          {template.body}
        </Text>
        
        <HStack justify="space-between" align="center">
          <HStack spacing={2}>
            <Avatar size="xs" name="User" src="" />
            <Text fontSize="xs" color="gray.500">
              {template.createdBy || 'Unknown'}
            </Text>
          </HStack>
          
          <Text fontSize="xs" color="gray.500">
            {template.updatedAt ? new Date(template.updatedAt).toLocaleDateString() : 'Recently'}
          </Text>
        </HStack>
      </CardBody>
    </Card>
  );
};

export const TemplatesPage: React.FC = () => {
  const [templates, setTemplates] = useState<EmailTemplate[]>([]);
  const [filteredTemplates, setFilteredTemplates] = useState<EmailTemplate[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [isLoading, setIsLoading] = useState(false);
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [editingTemplate, setEditingTemplate] = useState<EmailTemplate | null>(null);
  const [isEditMode, setIsEditMode] = useState(false);
  const toast = useToast();

  const categories = ['all', 'newsletter', 'welcome', 'promotional', 'transactional'];

  useEffect(() => {
    loadTemplates();
  }, []);

  useEffect(() => {
    filterTemplates();
  }, [templates, searchQuery, selectedCategory]);

  const loadTemplates = async () => {
    setIsLoading(true);
    try {
      const response = await TemplateService.getTemplates();
      // Convert Template[] to EmailTemplate[]
      const convertedTemplates: EmailTemplate[] = response.content.map(template => ({
        id: template.id,
        name: template.name,
        subject: template.subject,
        body: template.contentHtml, // Map contentHtml to body
        contentHtml: template.contentHtml,
        contentText: template.contentText,
        category: template.category,
        type: template.type,
        createdBy: template.createdBy,
        createdAt: template.createdAt,
        updatedAt: template.updatedAt,
        isDefault: template.isDefault,
        isActive: template.isActive,
        isPublic: template.isPublic,
        description: template.description,
        variables: template.variables,
        version: template.version,
        parentTemplateId: template.parentTemplateId,
        usageCount: template.usageCount,
        lastUsedAt: template.lastUsedAt,
        tags: template.tags,
        cssStyles: template.cssStyles,
        metadata: template.metadata,
        thumbnailUrl: template.thumbnailUrl,
      }));
      setTemplates(convertedTemplates);
    } catch (error) {
      toast({
        title: 'Error loading templates',
        description: error instanceof Error ? error.message : 'Failed to load templates',
        status: 'error',
        duration: 5000,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const filterTemplates = () => {
    let filtered = templates;

    if (searchQuery) {
      filtered = filtered.filter(
        template =>
          template.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
          template.subject?.toLowerCase().includes(searchQuery.toLowerCase()) ||
          template.body?.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    if (selectedCategory !== 'all') {
      filtered = filtered.filter(template => template.category === selectedCategory);
    }

    setFilteredTemplates(filtered);
  };

  const handleCreateTemplate = () => {
    setEditingTemplate({
      id: undefined,
      name: '',
      subject: '',
      body: '',
      category: 'newsletter',
      type: 'email',
      createdBy: 'Current User',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    });
    setIsEditMode(false);
    onOpen();
  };

  const handleEditTemplate = (template: EmailTemplate) => {
    setEditingTemplate(template);
    setIsEditMode(true);
    onOpen();
  };

  const handleDuplicateTemplate = (template: EmailTemplate) => {
    const duplicatedTemplate: EmailTemplate = {
      ...template,
      id: undefined,
      name: `${template.name} (Copy)`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    setEditingTemplate(duplicatedTemplate);
    setIsEditMode(false);
    onOpen();
  };

  const handleDeleteTemplate = async (template: EmailTemplate) => {
    if (window.confirm(`Are you sure you want to delete "${template.name}"?`)) {
      try {
        await TemplateService.deleteTemplate(template.id!);
        await loadTemplates();
        toast({
          title: 'Template deleted',
          description: 'Template has been successfully deleted',
          status: 'success',
          duration: 3000,
        });
      } catch (error) {
        toast({
          title: 'Error deleting template',
          description: error instanceof Error ? error.message : 'Failed to delete template',
          status: 'error',
          duration: 5000,
        });
      }
    }
  };

  const handleSaveTemplate = async () => {
    if (!editingTemplate) return;

    try {
      const templateData = {
        name: editingTemplate.name,
        subject: editingTemplate.subject,
        contentHtml: editingTemplate.body,
        contentText: editingTemplate.contentText,
        category: editingTemplate.category || 'CUSTOM',
        type: editingTemplate.type || 'HTML',
        description: editingTemplate.description,
        variables: editingTemplate.variables,
        isActive: editingTemplate.isActive !== false,
        isDefault: editingTemplate.isDefault || false,
        isPublic: editingTemplate.isPublic || false,
        cssStyles: editingTemplate.cssStyles,
        tags: editingTemplate.tags,
      };
      const savedTemplate = await TemplateService.createTemplate(templateData);
      await loadTemplates();
      onClose();
      toast({
        title: isEditMode ? 'Template updated' : 'Template created',
        description: `Template "${savedTemplate.name}" has been ${isEditMode ? 'updated' : 'created'} successfully`,
        status: 'success',
        duration: 3000,
      });
    } catch (error) {
      toast({
        title: 'Error saving template',
        description: error instanceof Error ? error.message : 'Failed to save template',
        status: 'error',
        duration: 5000,
      });
    }
  };

  return (
    <VStack spacing={8} align="stretch">
      {/* Header */}
      <Box>
        <Heading size="lg" color="gray.800" mb={2}>
          Email Templates
        </Heading>
        <Text color="gray.600">
          Create, manage, and organize your email templates for consistent messaging.
        </Text>
      </Box>

      {/* Controls */}
      <HStack justify="space-between" align="center">
        <HStack spacing={4} flex={1}>
          <InputGroup maxW="400px">
            <InputLeftElement pointerEvents="none">
              <Icon as={FiSearch as any} color="gray.300" />
            </InputLeftElement>
            <Input
              placeholder="Search templates..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </InputGroup>

          <HStack spacing={2}>
            {categories.map((category) => (
              <Tag
                key={category}
                colorScheme={selectedCategory === category ? 'brand' : 'gray'}
                variant={selectedCategory === category ? 'solid' : 'outline'}
                cursor="pointer"
                onClick={() => setSelectedCategory(category)}
                size="md"
              >
                <TagLabel>
                  {category === 'all' ? 'All' : category.charAt(0).toUpperCase() + category.slice(1)}
                </TagLabel>
              </Tag>
            ))}
          </HStack>
        </HStack>

        <Button
                      leftIcon={<Icon as={FiPlus as any} />}
          colorScheme="brand"
          onClick={handleCreateTemplate}
        >
          Create Template
        </Button>
      </HStack>

      {/* Templates Grid */}
      {isLoading ? (
        <Box textAlign="center" py={12}>
          <Text color="gray.500">Loading templates...</Text>
        </Box>
      ) : filteredTemplates.length === 0 ? (
        <Box textAlign="center" py={12}>
          <VStack spacing={4}>
            <Icon as={FiMail as any} boxSize={12} color="gray.300" />
            <Text color="gray.500" fontSize="lg">
              {searchQuery || selectedCategory !== 'all'
                ? 'No templates match your search criteria'
                : 'No templates found'}
            </Text>
            <Text color="gray.400">
              {searchQuery || selectedCategory !== 'all'
                ? 'Try adjusting your search or filters'
                : 'Create your first template to get started'}
            </Text>
          </VStack>
        </Box>
      ) : (
        <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(3, 1fr)' }} gap={6}>
          {filteredTemplates.map((template) => (
            <GridItem key={template.id}>
              <TemplateCard
                template={template}
                onEdit={handleEditTemplate}
                onDelete={handleDeleteTemplate}
                onDuplicate={handleDuplicateTemplate}
              />
            </GridItem>
          ))}
        </Grid>
      )}

      {/* Template Modal */}
      <Modal isOpen={isOpen} onClose={onClose} size="4xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>
            {isEditMode ? 'Edit Template' : 'Create Template'}
          </ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack spacing={6} align="stretch">
              <FormControl>
                <FormLabel>Template Name</FormLabel>
                <Input
                  value={editingTemplate?.name || ''}
                  onChange={(e) => setEditingTemplate(prev => prev ? { ...prev, name: e.target.value } : null)}
                  placeholder="Enter template name"
                />
              </FormControl>

              <FormControl>
                <FormLabel>Category</FormLabel>
                <HStack spacing={2}>
                  {categories.filter(cat => cat !== 'all').map((category) => (
                    <Tag
                      key={category}
                      colorScheme={editingTemplate?.category === category ? 'brand' : 'gray'}
                      variant={editingTemplate?.category === category ? 'solid' : 'outline'}
                      cursor="pointer"
                      onClick={() => setEditingTemplate(prev => prev ? { ...prev, category } : null)}
                      size="md"
                    >
                      <TagLabel>
                        {category.charAt(0).toUpperCase() + category.slice(1)}
                      </TagLabel>
                    </Tag>
                  ))}
                </HStack>
              </FormControl>

              <FormControl>
                <FormLabel>Subject Line</FormLabel>
                <Input
                  value={editingTemplate?.subject || ''}
                  onChange={(e) => setEditingTemplate(prev => prev ? { ...prev, subject: e.target.value } : null)}
                  placeholder="Enter email subject"
                />
              </FormControl>

              <FormControl>
                <FormLabel>Email Body</FormLabel>
                <Textarea
                  value={editingTemplate?.body || ''}
                  onChange={(e) => setEditingTemplate(prev => prev ? { ...prev, body: e.target.value } : null)}
                  placeholder="Enter email content..."
                  rows={12}
                  resize="vertical"
                />
              </FormControl>
            </VStack>
          </ModalBody>

          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={onClose}>
              Cancel
            </Button>
            <Button colorScheme="brand" onClick={handleSaveTemplate}>
              {isEditMode ? 'Update Template' : 'Create Template'}
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </VStack>
  );
};
