import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Flex,
  Heading,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  Badge,
  IconButton,
  useDisclosure,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  ModalCloseButton,
  FormControl,
  FormLabel,
  Input,
  Textarea,
  Switch,
  useToast,
  Tooltip,
  Text,
  AlertDialog,
  AlertDialogBody,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogContent,
  AlertDialogOverlay,
  Spinner,
  InputGroup,
  InputRightElement,
  useClipboard,
} from '@chakra-ui/react';
import { AddIcon, DeleteIcon, RepeatIcon, CopyIcon } from '@chakra-ui/icons';
import { apiKeyService, ApiKey, CreateApiKeyRequest } from '../../api/apiKeyService';
import { format } from 'date-fns';

export const ApiKeyManager: React.FC = () => {
  const [apiKeys, setApiKeys] = useState<ApiKey[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedApiKey, setSelectedApiKey] = useState<ApiKey | null>(null);
  const [newApiKey, setNewApiKey] = useState<CreateApiKeyRequest>({
    name: '',
    description: '',
  });
  const [showNewApiKey, setShowNewApiKey] = useState<string | null>(null);
  const { onCopy, hasCopied } = useClipboard('');
  const toast = useToast();

  // Modal states
  const { isOpen: isCreateOpen, onOpen: onCreateOpen, onClose: onCreateClose } = useDisclosure();
  const { isOpen: isDeleteOpen, onOpen: onDeleteOpen, onClose: onDeleteClose } = useDisclosure();
  const { isOpen: isRegenerateOpen, onOpen: onRegenerateOpen, onClose: onRegenerateClose } = useDisclosure();
  const cancelRef = React.useRef<HTMLButtonElement>(null);

  useEffect(() => {
    fetchApiKeys();
  }, []);

  const fetchApiKeys = async () => {
    setLoading(true);
    try {
      const response = await apiKeyService.getAllApiKeys();
      if (response.success && response.data) {
        setApiKeys(response.data);
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to fetch API keys',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'An unexpected error occurred',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCreateApiKey = async () => {
    try {
      const response = await apiKeyService.createApiKey(newApiKey);
      if (response.success && response.data) {
        setShowNewApiKey(response.data.apiKey);
        setApiKeys([response.data, ...apiKeys]);
        setNewApiKey({ name: '', description: '' });
        onCreateClose();
        toast({
          title: 'Success',
          description: 'API key created successfully',
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to create API key',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'An unexpected error occurred',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleDeleteApiKey = async () => {
    if (!selectedApiKey) return;

    try {
      const response = await apiKeyService.deleteApiKey(selectedApiKey.id);
      if (response.success) {
        setApiKeys(apiKeys.filter(key => key.id !== selectedApiKey.id));
        setSelectedApiKey(null);
        onDeleteClose();
        toast({
          title: 'Success',
          description: 'API key deleted successfully',
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to delete API key',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'An unexpected error occurred',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleRegenerateApiKey = async () => {
    if (!selectedApiKey) return;

    try {
      const response = await apiKeyService.regenerateApiKey(selectedApiKey.id);
      if (response.success && response.data) {
        setApiKeys(apiKeys.map(key => key.id === selectedApiKey.id ? response.data! : key));
        setShowNewApiKey(response.data.apiKey);
        setSelectedApiKey(null);
        onRegenerateClose();
        toast({
          title: 'Success',
          description: 'API key regenerated successfully',
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to regenerate API key',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'An unexpected error occurred',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleToggleApiKeyStatus = async (id: number, enabled: boolean) => {
    try {
      const response = await apiKeyService.toggleApiKeyStatus(id, enabled);
      if (response.success && response.data) {
        setApiKeys(apiKeys.map(key => key.id === id ? response.data! : key));
        toast({
          title: 'Success',
          description: `API key ${enabled ? 'enabled' : 'disabled'} successfully`,
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to update API key status',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'An unexpected error occurred',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleCopyApiKey = (apiKey: string) => {
    onCopy(apiKey);
    toast({
      title: 'Copied',
      description: 'API key copied to clipboard',
      status: 'info',
      duration: 2000,
      isClosable: true,
    });
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return 'N/A';
    return format(new Date(dateString), 'MMM d, yyyy HH:mm');
  };

  if (loading) {
    return (
      <Flex justify="center" align="center" height="200px">
        <Spinner size="xl" />
      </Flex>
    );
  }

  return (
    <Box>
      <Flex justify="space-between" align="center" mb={4}>
        <Heading size="md">API Keys</Heading>
        <Button leftIcon={<AddIcon />} colorScheme="blue" onClick={onCreateOpen}>
          Create API Key
        </Button>
      </Flex>

      {showNewApiKey && (
        <Box p={4} bg="green.50" borderRadius="md" mb={4}>
          <Flex align="center" justify="space-between">
            <Text fontWeight="bold">New API Key (copy this now, it won't be shown again):</Text>
            <IconButton
              aria-label="Copy API Key"
              icon={<CopyIcon />}
              size="sm"
              onClick={() => handleCopyApiKey(showNewApiKey)}
            />
          </Flex>
          <Text mt={2} fontFamily="monospace" fontSize="sm">
            {showNewApiKey}
          </Text>
          <Button size="sm" mt={2} onClick={() => setShowNewApiKey(null)}>
            I've copied it
          </Button>
        </Box>
      )}

      {apiKeys.length === 0 ? (
        <Box p={4} textAlign="center">
          <Text>No API keys found. Create your first API key to get started.</Text>
        </Box>
      ) : (
        <Table variant="simple">
          <Thead>
            <Tr>
              <Th>Name</Th>
              <Th>Created</Th>
              <Th>Last Used</Th>
              <Th>Status</Th>
              <Th>Actions</Th>
            </Tr>
          </Thead>
          <Tbody>
            {apiKeys.map((apiKey) => (
              <Tr key={apiKey.id}>
                <Td>
                  <Text fontWeight="bold">{apiKey.name}</Text>
                  {apiKey.description && (
                    <Text fontSize="sm" color="gray.600">
                      {apiKey.description}
                    </Text>
                  )}
                </Td>
                <Td>
                  <Text fontSize="sm">{formatDate(apiKey.createdAt)}</Text>
                  <Text fontSize="xs" color="gray.500">
                    by {apiKey.createdBy}
                  </Text>
                </Td>
                <Td>{apiKey.lastUsedAt ? formatDate(apiKey.lastUsedAt) : 'Never'}</Td>
                <Td>
                  <Flex align="center">
                    <Switch
                      isChecked={apiKey.enabled}
                      onChange={() => handleToggleApiKeyStatus(apiKey.id, !apiKey.enabled)}
                      mr={2}
                    />
                    <Badge colorScheme={apiKey.enabled ? 'green' : 'red'}>
                      {apiKey.enabled ? 'Active' : 'Inactive'}
                    </Badge>
                  </Flex>
                </Td>
                <Td>
                  <Flex>
                    <Tooltip label="Regenerate API Key">
                      <IconButton
                        aria-label="Regenerate API Key"
                        icon={<RepeatIcon />}
                        size="sm"
                        mr={2}
                        onClick={() => {
                          setSelectedApiKey(apiKey);
                          onRegenerateOpen();
                        }}
                      />
                    </Tooltip>
                    <Tooltip label="Delete API Key">
                      <IconButton
                        aria-label="Delete API Key"
                        icon={<DeleteIcon />}
                        size="sm"
                        colorScheme="red"
                        onClick={() => {
                          setSelectedApiKey(apiKey);
                          onDeleteOpen();
                        }}
                      />
                    </Tooltip>
                  </Flex>
                </Td>
              </Tr>
            ))}
          </Tbody>
        </Table>
      )}

      {/* Create API Key Modal */}
      <Modal isOpen={isCreateOpen} onClose={onCreateClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Create API Key</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <FormControl mb={4} isRequired>
              <FormLabel>Name</FormLabel>
              <Input
                value={newApiKey.name}
                onChange={(e) => setNewApiKey({ ...newApiKey, name: e.target.value })}
                placeholder="e.g., Production API"
              />
            </FormControl>
            <FormControl mb={4}>
              <FormLabel>Description</FormLabel>
              <Textarea
                value={newApiKey.description || ''}
                onChange={(e) => setNewApiKey({ ...newApiKey, description: e.target.value })}
                placeholder="Optional description"
              />
            </FormControl>
            <FormControl mb={4}>
              <FormLabel>Expiration Date (Optional)</FormLabel>
              <Input
                type="datetime-local"
                onChange={(e) =>
                  setNewApiKey({ ...newApiKey, expiresAt: e.target.value ? new Date(e.target.value).toISOString() : undefined })
                }
              />
            </FormControl>
          </ModalBody>
          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={onCreateClose}>
              Cancel
            </Button>
            <Button colorScheme="blue" onClick={handleCreateApiKey} isDisabled={!newApiKey.name}>
              Create
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Delete API Key Confirmation Dialog */}
      <AlertDialog isOpen={isDeleteOpen} leastDestructiveRef={cancelRef} onClose={onDeleteClose}>
        <AlertDialogOverlay>
          <AlertDialogContent>
            <AlertDialogHeader fontSize="lg" fontWeight="bold">
              Delete API Key
            </AlertDialogHeader>
            <AlertDialogBody>
              Are you sure you want to delete the API key "{selectedApiKey?.name}"? This action cannot be undone.
            </AlertDialogBody>
            <AlertDialogFooter>
              <Button ref={cancelRef} onClick={onDeleteClose}>
                Cancel
              </Button>
              <Button colorScheme="red" onClick={handleDeleteApiKey} ml={3}>
                Delete
              </Button>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialogOverlay>
      </AlertDialog>

      {/* Regenerate API Key Confirmation Dialog */}
      <AlertDialog isOpen={isRegenerateOpen} leastDestructiveRef={cancelRef} onClose={onRegenerateClose}>
        <AlertDialogOverlay>
          <AlertDialogContent>
            <AlertDialogHeader fontSize="lg" fontWeight="bold">
              Regenerate API Key
            </AlertDialogHeader>
            <AlertDialogBody>
              Are you sure you want to regenerate the API key "{selectedApiKey?.name}"? The current key will be invalidated immediately.
            </AlertDialogBody>
            <AlertDialogFooter>
              <Button ref={cancelRef} onClick={onRegenerateClose}>
                Cancel
              </Button>
              <Button colorScheme="blue" onClick={handleRegenerateApiKey} ml={3}>
                Regenerate
              </Button>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialogOverlay>
      </AlertDialog>
    </Box>
  );
};
