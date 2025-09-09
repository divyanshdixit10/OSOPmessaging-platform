import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Button,
  Card,
  CardBody,
  CardHeader,
  Heading,
  Badge,
  useColorModeValue,
  Flex,
  Spacer,
  useToast,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
  FormControl,
  FormLabel,
  Input,
  Select,
  Textarea,
  useDisclosure,
  Alert,
  AlertIcon,
  AlertTitle,
  AlertDescription,
  Stat,
  StatLabel,
  StatNumber,
  StatHelpText,
  Icon,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  Spinner,
  Grid,
  GridItem,
  Image,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  IconButton,
  Divider,
  Tag,
  TagLabel,
  TagCloseButton,
  InputGroup,
  InputLeftElement,
  Stack,
  Wrap,
  WrapItem,
} from '@chakra-ui/react';
import {
  FiPlus,
  FiEdit,
  FiTrash2,
  FiCopy,
  FiEye,
  FiDownload,
  FiUpload,
  FiSearch,
  FiFilter,
  FiGrid,
  FiList,
  FiMoreVertical,
  FiCalendar,
  FiUser,
  FiTag,
  FiBarChart,
  FiTrendingUp,
  FiClock,
  FiStar,
  FiCode,
  FiImage,
  FiType,
} from 'react-icons/fi';
import TemplateService, { 
  Template, 
  CreateTemplateRequest,
  UpdateTemplateRequest,
  TemplateVersion,
  TemplateStatistics,
  TemplateFilters 
} from '../api/templateService';
import RichTextEditor from '../components/RichTextEditor';

interface TemplateCardProps {
  template: Template;
  onEdit: (template: Template) => void;
  onDelete: (template: Template) => void;
  onClone: (template: Template) => void;
  onPreview: (template: Template) => void;
  onExport: (template: Template) => void;
  onViewVersions: (template: Template) => void;
}

const TemplateCard: React.FC<TemplateCardProps> = ({
  template,
  onEdit,
  onDelete,
  onClone,
  onPreview,
  onExport,
  onViewVersions
}) => {
  const getCategoryColor = (category: string) => {
    switch (category.toLowerCase()) {
      case 'newsletter': return 'blue';
      case 'promotion': return 'green';
      case 'transactional': return 'purple';
      case 'welcome': return 'orange';
      case 'follow-up': return 'teal';
      case 'announcement': return 'red';
      case 'custom': return 'gray';
      default: return 'gray';
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type.toLowerCase()) {
      case 'html': return FiCode;
      case 'text': return FiType;
      case 'rich_text': return FiEdit;
      default: return FiType;
    }
  };

  return (
    <Card>
      <CardHeader>
        <HStack justify="space-between" align="flex-start">
          <VStack align="flex-start" spacing={2}>
            <Heading size="md" color="gray.800">
              {template.name}
            </Heading>
            <Text fontSize="sm" color="gray.600" noOfLines={2}>
              {template.description || template.subject}
            </Text>
            <HStack spacing={2}>
              <Badge colorScheme={getCategoryColor(template.category)} size="sm">
                {template.category}
              </Badge>
              <Badge variant="outline" size="sm">
                <Icon as={getTypeIcon(template.type) as any} mr={1} />
                {template.type}
              </Badge>
              {template.isPublic && (
                <Badge colorScheme="green" size="sm">
                  Public
                </Badge>
              )}
            </HStack>
          </VStack>
          
          <Menu>
            <MenuButton
              as={IconButton}
              icon={<Icon as={FiMoreVertical as any} />}
              variant="ghost"
              size="sm"
            />
            <MenuList>
              <MenuItem icon={<Icon as={FiEdit as any} />} onClick={() => onEdit(template)}>
                Edit
              </MenuItem>
              <MenuItem icon={<Icon as={FiEye as any} />} onClick={() => onPreview(template)}>
                Preview
              </MenuItem>
              <MenuItem icon={<Icon as={FiCopy as any} />} onClick={() => onClone(template)}>
                Clone
              </MenuItem>
              <MenuItem icon={<Icon as={FiDownload as any} />} onClick={() => onExport(template)}>
                Export
              </MenuItem>
              <MenuItem icon={<Icon as={FiClock as any} />} onClick={() => onViewVersions(template)}>
                Versions
              </MenuItem>
              <Divider />
              <MenuItem 
                icon={<Icon as={FiTrash2 as any} />} 
                onClick={() => onDelete(template)}
                color="red.500"
              >
                Delete
              </MenuItem>
            </MenuList>
          </Menu>
        </HStack>
      </CardHeader>
      
      <CardBody>
        {/* Template Stats */}
        <HStack spacing={4} justify="space-between" mb={4}>
          <Stat size="sm">
            <StatLabel>Usage</StatLabel>
            <StatNumber color="blue.500">{template.usageCount}</StatNumber>
          </Stat>
          <Stat size="sm">
            <StatLabel>Version</StatLabel>
            <StatNumber color="gray.500">v{template.version}</StatNumber>
          </Stat>
          <Stat size="sm">
            <StatLabel>Created</StatLabel>
            <StatHelpText fontSize="xs">
              {new Date(template.createdAt).toLocaleDateString()}
            </StatHelpText>
          </Stat>
        </HStack>

        {/* Tags */}
        {template.tags && template.tags.length > 0 && (
          <Wrap spacing={1} mb={4}>
            {template.tags.map((tag, index) => (
              <WrapItem key={index}>
                <Tag size="sm" variant="subtle" colorScheme="blue">
                  <TagLabel>{tag}</TagLabel>
                </Tag>
              </WrapItem>
            ))}
          </Wrap>
        )}

        {/* Action Buttons */}
        <HStack spacing={2}>
          <Button
            size="sm"
            colorScheme="blue"
            leftIcon={<Icon as={FiEdit as any} />}
            onClick={() => onEdit(template)}
            flex={1}
          >
            Edit
          </Button>
          <Button
            size="sm"
            variant="outline"
            leftIcon={<Icon as={FiEye as any} />}
            onClick={() => onPreview(template)}
            flex={1}
          >
            Preview
          </Button>
        </HStack>
      </CardBody>
    </Card>
  );
};

export const EnhancedTemplatesPage: React.FC = () => {
  const [templates, setTemplates] = useState<Template[]>([]);
  const [statistics, setStatistics] = useState<TemplateStatistics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [selectedTemplate, setSelectedTemplate] = useState<Template | null>(null);
  const [templateVersions, setTemplateVersions] = useState<TemplateVersion[]>([]);
  
  // Filters
  const [filters, setFilters] = useState<TemplateFilters>({
    search: '',
    category: '',
    type: '',
    isActive: true,
    page: 0,
    size: 20,
    sortBy: 'createdAt',
    sortDir: 'desc'
  });
  
  const toast = useToast();
  
  // Modals
  const { isOpen: isCreateOpen, onOpen: onCreateOpen, onClose: onCreateClose } = useDisclosure();
  const { isOpen: isEditOpen, onOpen: onEditOpen, onClose: onEditClose } = useDisclosure();
  const { isOpen: isPreviewOpen, onOpen: onPreviewOpen, onClose: onPreviewClose } = useDisclosure();
  const { isOpen: isVersionsOpen, onOpen: onVersionsOpen, onClose: onVersionsClose } = useDisclosure();
  const { isOpen: isCloneOpen, onOpen: onCloneOpen, onClose: onCloneClose } = useDisclosure();
  
  // Form states
  const [createForm, setCreateForm] = useState<CreateTemplateRequest>({
    name: '',
    subject: '',
    contentHtml: '',
    category: 'CUSTOM',
    type: 'HTML',
    description: '',
    tags: [],
    isPublic: false
  });
  
  const [editForm, setEditForm] = useState<UpdateTemplateRequest>({
    name: '',
    subject: '',
    contentHtml: '',
    changeDescription: ''
  });
  
  const [cloneForm, setCloneForm] = useState({
    name: ''
  });

  // Fetch templates and statistics
  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      
      const [templatesData, statsData] = await Promise.all([
        TemplateService.getTemplates(filters),
        TemplateService.getTemplateStatistics()
      ]);
      
      setTemplates(templatesData.content);
      setStatistics(statsData);
      
    } catch (err) {
      console.error('Error fetching templates:', err);
      setError('Failed to load templates');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // Template actions
  const handleCreateTemplate = async () => {
    try {
      await TemplateService.createTemplate(createForm);
      toast({
        title: 'Template Created',
        description: 'Template has been created successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      onCreateClose();
      setCreateForm({
        name: '',
        subject: '',
        contentHtml: '',
        category: 'CUSTOM',
        type: 'HTML',
        description: '',
        tags: [],
        isPublic: false
      });
      fetchData();
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to create template',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleEditTemplate = async () => {
    if (!selectedTemplate) return;
    
    try {
      await TemplateService.updateTemplate(selectedTemplate.id, editForm);
      toast({
        title: 'Template Updated',
        description: 'Template has been updated successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      onEditClose();
      fetchData();
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to update template',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleDeleteTemplate = async (template: Template) => {
    try {
      await TemplateService.deleteTemplate(template.id);
      toast({
        title: 'Template Deleted',
        description: 'Template has been deleted successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      fetchData();
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to delete template',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleCloneTemplate = async () => {
    if (!selectedTemplate) return;
    
    try {
      await TemplateService.cloneTemplate(selectedTemplate.id, cloneForm.name);
      toast({
        title: 'Template Cloned',
        description: 'Template has been cloned successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      onCloneClose();
      setCloneForm({ name: '' });
      fetchData();
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to clone template',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleExportTemplate = async (template: Template) => {
    try {
      const exported = await TemplateService.exportTemplate(template.id);
      
      // Create and download file
      const blob = new Blob([exported], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `template_${template.name.replace(/\s+/g, '_')}.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      
      toast({
        title: 'Template Exported',
        description: 'Template has been exported successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to export template',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleViewVersions = async (template: Template) => {
    try {
      const versions = await TemplateService.getTemplateVersions(template.id);
      setTemplateVersions(versions);
      setSelectedTemplate(template);
      onVersionsOpen();
    } catch (err) {
      toast({
        title: 'Error',
        description: 'Failed to load template versions',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handlePreviewTemplate = (template: Template) => {
    setSelectedTemplate(template);
    onPreviewOpen();
  };

  const handleEditTemplateClick = (template: Template) => {
    setSelectedTemplate(template);
    setEditForm({
      name: template.name,
      subject: template.subject,
      contentHtml: template.contentHtml,
      changeDescription: ''
    });
    onEditOpen();
  };

  const handleCloneTemplateClick = (template: Template) => {
    setSelectedTemplate(template);
    setCloneForm({ name: `${template.name} (Copy)` });
    onCloneOpen();
  };

  if (loading) {
    return (
      <VStack spacing={4} align="center" justify="center" h="400px">
        <Spinner size="xl" color="blue.500" />
        <Text>Loading templates...</Text>
      </VStack>
    );
  }

  if (error) {
    return (
      <Alert status="error">
        <AlertIcon />
        <AlertTitle>Error loading templates!</AlertTitle>
        <AlertDescription>{error}</AlertDescription>
      </Alert>
    );
  }

  return (
    <VStack spacing={8} align="stretch">
      {/* Header */}
      <Box>
        <HStack justify="space-between" align="flex-start" mb={4}>
          <Box>
            <Heading size="lg" color="gray.800" mb={2}>
              Enhanced Template Management
            </Heading>
            <Text color="gray.600">
              Create, edit, and manage professional email templates with our advanced editor.
            </Text>
          </Box>
          <Button
            colorScheme="blue"
            leftIcon={<Icon as={FiPlus as any} />}
            onClick={onCreateOpen}
          >
            Create Template
          </Button>
        </HStack>

        {/* Statistics */}
        {statistics && (
          <Grid templateColumns="repeat(auto-fit, minmax(200px, 1fr))" gap={4} mb={6}>
            <GridItem>
              <Card>
                <CardBody>
                  <Stat>
                    <StatLabel>Total Templates</StatLabel>
                    <StatNumber color="blue.500">{statistics.totalTemplates}</StatNumber>
                    <StatHelpText>
                      <Icon as={FiBarChart as any} mr={1} />
                      All templates
                    </StatHelpText>
                  </Stat>
                </CardBody>
              </Card>
            </GridItem>
            <GridItem>
              <Card>
                <CardBody>
                  <Stat>
                    <StatLabel>Active Templates</StatLabel>
                    <StatNumber color="green.500">{statistics.activeTemplates}</StatNumber>
                    <StatHelpText>
                      <Icon as={FiTrendingUp as any} mr={1} />
                      Currently active
                    </StatHelpText>
                  </Stat>
                </CardBody>
              </Card>
            </GridItem>
            <GridItem>
              <Card>
                <CardBody>
                  <Stat>
                    <StatLabel>Categories</StatLabel>
                    <StatNumber color="purple.500">{statistics.categories.length}</StatNumber>
                    <StatHelpText>
                      <Icon as={FiTag as any} mr={1} />
                      Available categories
                    </StatHelpText>
                  </Stat>
                </CardBody>
              </Card>
            </GridItem>
          </Grid>
        )}
      </Box>

      {/* Filters and Controls */}
      <Card>
        <CardBody>
          <VStack spacing={4}>
            <HStack spacing={4} w="full">
              <InputGroup flex={1}>
                <InputLeftElement>
                  <Icon as={FiSearch as any} color="gray.400" />
                </InputLeftElement>
                <Input
                  placeholder="Search templates..."
                  value={filters.search}
                  onChange={(e) => setFilters(prev => ({ ...prev, search: e.target.value }))}
                />
              </InputGroup>
              
              <Select
                placeholder="All Categories"
                value={filters.category}
                onChange={(e) => setFilters(prev => ({ ...prev, category: e.target.value }))}
                w="200px"
              >
                {statistics?.categories.map(category => (
                  <option key={category} value={category}>{category}</option>
                ))}
              </Select>
              
              <Select
                placeholder="All Types"
                value={filters.type}
                onChange={(e) => setFilters(prev => ({ ...prev, type: e.target.value }))}
                w="150px"
              >
                {statistics?.types.map(type => (
                  <option key={type} value={type}>{type}</option>
                ))}
              </Select>
              
              <HStack spacing={2}>
                <IconButton
                  icon={<Icon as={FiGrid as any} />}
                  variant={viewMode === 'grid' ? 'solid' : 'outline'}
                  colorScheme={viewMode === 'grid' ? 'blue' : 'gray'}
                  onClick={() => setViewMode('grid')}
                  aria-label="Grid view"
                />
                <IconButton
                  icon={<Icon as={FiList as any} />}
                  variant={viewMode === 'list' ? 'solid' : 'outline'}
                  colorScheme={viewMode === 'list' ? 'blue' : 'gray'}
                  onClick={() => setViewMode('list')}
                  aria-label="List view"
                />
              </HStack>
            </HStack>
          </VStack>
        </CardBody>
      </Card>

      {/* Templates Grid/List */}
      {viewMode === 'grid' ? (
        <Grid templateColumns="repeat(auto-fill, minmax(350px, 1fr))" gap={6}>
          {templates.map((template) => (
            <GridItem key={template.id}>
              <TemplateCard
                template={template}
                onEdit={handleEditTemplateClick}
                onDelete={handleDeleteTemplate}
                onClone={handleCloneTemplateClick}
                onPreview={handlePreviewTemplate}
                onExport={handleExportTemplate}
                onViewVersions={handleViewVersions}
              />
            </GridItem>
          ))}
        </Grid>
      ) : (
        <Card>
          <Table variant="simple">
            <Thead>
              <Tr>
                <Th>Name</Th>
                <Th>Category</Th>
                <Th>Type</Th>
                <Th>Usage</Th>
                <Th>Created</Th>
                <Th>Actions</Th>
              </Tr>
            </Thead>
            <Tbody>
              {templates.map((template) => (
                <Tr key={template.id}>
                  <Td>
                    <VStack align="flex-start" spacing={1}>
                      <Text fontWeight="medium">{template.name}</Text>
                      <Text fontSize="sm" color="gray.500" noOfLines={1}>
                        {template.subject}
                      </Text>
                    </VStack>
                  </Td>
                  <Td>
                    <Badge colorScheme="blue" variant="subtle">
                      {template.category}
                    </Badge>
                  </Td>
                  <Td>
                    <Badge variant="outline">
                      {template.type}
                    </Badge>
                  </Td>
                  <Td>{template.usageCount}</Td>
                  <Td>{new Date(template.createdAt).toLocaleDateString()}</Td>
                  <Td>
                    <HStack spacing={1}>
                      <IconButton
                        icon={<Icon as={FiEdit as any} />}
                        size="sm"
                        variant="ghost"
                        onClick={() => handleEditTemplateClick(template)}
                        aria-label="Edit template"
                      />
                      <IconButton
                        icon={<Icon as={FiEye as any} />}
                        size="sm"
                        variant="ghost"
                        onClick={() => handlePreviewTemplate(template)}
                        aria-label="Preview template"
                      />
                      <IconButton
                        icon={<Icon as={FiCopy as any} />}
                        size="sm"
                        variant="ghost"
                        onClick={() => handleCloneTemplateClick(template)}
                        aria-label="Clone template"
                      />
                    </HStack>
                  </Td>
                </Tr>
              ))}
            </Tbody>
          </Table>
        </Card>
      )}

      {/* Create Template Modal */}
      <Modal isOpen={isCreateOpen} onClose={onCreateClose} size="6xl">
        <ModalOverlay />
        <ModalContent maxW="90vw" maxH="90vh">
          <ModalHeader>Create New Template</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <VStack spacing={6} align="stretch">
              <HStack spacing={4}>
                <FormControl flex={1}>
                  <FormLabel>Template Name</FormLabel>
                  <Input
                    value={createForm.name}
                    onChange={(e) => setCreateForm(prev => ({ ...prev, name: e.target.value }))}
                    placeholder="Enter template name"
                  />
                </FormControl>
                <FormControl flex={1}>
                  <FormLabel>Subject</FormLabel>
                  <Input
                    value={createForm.subject}
                    onChange={(e) => setCreateForm(prev => ({ ...prev, subject: e.target.value }))}
                    placeholder="Enter email subject"
                  />
                </FormControl>
              </HStack>
              
              <HStack spacing={4}>
                <FormControl flex={1}>
                  <FormLabel>Category</FormLabel>
                  <Select
                    value={createForm.category}
                    onChange={(e) => setCreateForm(prev => ({ ...prev, category: e.target.value }))}
                  >
                    <option value="NEWSLETTER">Newsletter</option>
                    <option value="PROMOTION">Promotion</option>
                    <option value="TRANSACTIONAL">Transactional</option>
                    <option value="WELCOME">Welcome</option>
                    <option value="FOLLOW_UP">Follow-up</option>
                    <option value="ANNOUNCEMENT">Announcement</option>
                    <option value="CUSTOM">Custom</option>
                  </Select>
                </FormControl>
                <FormControl flex={1}>
                  <FormLabel>Type</FormLabel>
                  <Select
                    value={createForm.type}
                    onChange={(e) => setCreateForm(prev => ({ ...prev, type: e.target.value }))}
                  >
                    <option value="HTML">HTML</option>
                    <option value="TEXT">Text</option>
                    <option value="RICH_TEXT">Rich Text</option>
                  </Select>
                </FormControl>
              </HStack>
              
              <FormControl>
                <FormLabel>Description</FormLabel>
                <Textarea
                  value={createForm.description}
                  onChange={(e) => setCreateForm(prev => ({ ...prev, description: e.target.value }))}
                  placeholder="Enter template description"
                  rows={2}
                />
              </FormControl>
              
              <FormControl>
                <FormLabel>Content</FormLabel>
                <RichTextEditor
                  value={createForm.contentHtml}
                  onChange={(value) => setCreateForm(prev => ({ ...prev, contentHtml: value }))}
                  height="400px"
                  placeholder="Start creating your email template..."
                />
              </FormControl>
              
              <HStack spacing={4} w="full">
                <Button onClick={onCreateClose} flex={1}>
                  Cancel
                </Button>
                <Button colorScheme="blue" onClick={handleCreateTemplate} flex={1}>
                  Create Template
                </Button>
              </HStack>
            </VStack>
          </ModalBody>
        </ModalContent>
      </Modal>

      {/* Edit Template Modal */}
      <Modal isOpen={isEditOpen} onClose={onEditClose} size="6xl">
        <ModalOverlay />
        <ModalContent maxW="90vw" maxH="90vh">
          <ModalHeader>Edit Template</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <VStack spacing={6} align="stretch">
              <HStack spacing={4}>
                <FormControl flex={1}>
                  <FormLabel>Template Name</FormLabel>
                  <Input
                    value={editForm.name}
                    onChange={(e) => setEditForm(prev => ({ ...prev, name: e.target.value }))}
                  />
                </FormControl>
                <FormControl flex={1}>
                  <FormLabel>Subject</FormLabel>
                  <Input
                    value={editForm.subject}
                    onChange={(e) => setEditForm(prev => ({ ...prev, subject: e.target.value }))}
                  />
                </FormControl>
              </HStack>
              
              <FormControl>
                <FormLabel>Change Description</FormLabel>
                <Input
                  value={editForm.changeDescription}
                  onChange={(e) => setEditForm(prev => ({ ...prev, changeDescription: e.target.value }))}
                  placeholder="Describe what you changed"
                />
              </FormControl>
              
              <FormControl>
                <FormLabel>Content</FormLabel>
                <RichTextEditor
                  value={editForm.contentHtml}
                  onChange={(value) => setEditForm(prev => ({ ...prev, contentHtml: value }))}
                  height="400px"
                  autoSave={true}
                  onSave={handleEditTemplate}
                />
              </FormControl>
              
              <HStack spacing={4} w="full">
                <Button onClick={onEditClose} flex={1}>
                  Cancel
                </Button>
                <Button colorScheme="blue" onClick={handleEditTemplate} flex={1}>
                  Save Changes
                </Button>
              </HStack>
            </VStack>
          </ModalBody>
        </ModalContent>
      </Modal>

      {/* Preview Modal */}
      <Modal isOpen={isPreviewOpen} onClose={onPreviewClose} size="4xl">
        <ModalOverlay />
        <ModalContent maxW="90vw" maxH="90vh">
          <ModalHeader>Template Preview</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            {selectedTemplate && (
              <VStack spacing={4} align="stretch">
                <HStack justify="space-between">
                  <VStack align="flex-start" spacing={1}>
                    <Text fontSize="lg" fontWeight="bold">{selectedTemplate.name}</Text>
                    <Text fontSize="sm" color="gray.600">{selectedTemplate.subject}</Text>
                  </VStack>
                  <Badge colorScheme="blue">{selectedTemplate.category}</Badge>
                </HStack>
                
                <Box
                  border="1px"
                  borderColor="gray.200"
                  borderRadius="md"
                  p={4}
                  bg="white"
                  maxH="60vh"
                  overflow="auto"
                >
                  <div dangerouslySetInnerHTML={{ __html: selectedTemplate.contentHtml }} />
                </Box>
              </VStack>
            )}
          </ModalBody>
        </ModalContent>
      </Modal>

      {/* Clone Modal */}
      <Modal isOpen={isCloneOpen} onClose={onCloneClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Clone Template</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <VStack spacing={4}>
              <FormControl>
                <FormLabel>New Template Name</FormLabel>
                <Input
                  value={cloneForm.name}
                  onChange={(e) => setCloneForm(prev => ({ ...prev, name: e.target.value }))}
                  placeholder="Enter new template name"
                />
              </FormControl>
              
              <HStack spacing={4} w="full">
                <Button onClick={onCloneClose} flex={1}>
                  Cancel
                </Button>
                <Button colorScheme="blue" onClick={handleCloneTemplate} flex={1}>
                  Clone Template
                </Button>
              </HStack>
            </VStack>
          </ModalBody>
        </ModalContent>
      </Modal>

      {/* Versions Modal */}
      <Modal isOpen={isVersionsOpen} onClose={onVersionsClose} size="4xl">
        <ModalOverlay />
        <ModalContent maxW="90vw" maxH="90vh">
          <ModalHeader>Template Versions</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <Table variant="simple">
              <Thead>
                <Tr>
                  <Th>Version</Th>
                  <Th>Created By</Th>
                  <Th>Created At</Th>
                  <Th>Description</Th>
                  <Th>Size</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {templateVersions.map((version) => (
                  <Tr key={version.id}>
                    <Td>
                      <HStack>
                        <Text>v{version.versionNumber}</Text>
                        {version.isCurrentVersion && (
                          <Badge colorScheme="green" size="sm">Current</Badge>
                        )}
                      </HStack>
                    </Td>
                    <Td>{version.createdBy}</Td>
                    <Td>{new Date(version.createdAt).toLocaleString()}</Td>
                    <Td>{version.changeDescription || 'No description'}</Td>
                    <Td>{(version.fileSize / 1024).toFixed(1)} KB</Td>
                    <Td>
                      <Button
                        size="sm"
                        variant="outline"
                        isDisabled={version.isCurrentVersion}
                      >
                        Revert
                      </Button>
                    </Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </ModalBody>
        </ModalContent>
      </Modal>
    </VStack>
  );
};
