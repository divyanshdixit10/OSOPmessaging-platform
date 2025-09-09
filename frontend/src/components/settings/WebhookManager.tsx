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
  CheckboxGroup,
  Checkbox,
  Stack,
  FormHelperText,
  InputGroup,
  InputRightElement,
  Button as ChakraButton,
  useClipboard,
} from '@chakra-ui/react';
import { AddIcon, DeleteIcon, EditIcon, CopyIcon } from '@chakra-ui/icons';
import { webhookService, WebhookEndpoint, WebhookEndpointRequest } from '../../api/webhookService';
import { format } from 'date-fns';

export const WebhookManager: React.FC = () => {
  const [webhooks, setWebhooks] = useState<WebhookEndpoint[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedWebhook, setSelectedWebhook] = useState<WebhookEndpoint | null>(null);
  const [newWebhook, setNewWebhook] = useState<WebhookEndpointRequest>({
    name: '',
    url: '',
    description: '',
    events: [],
  });
  const [availableEvents, setAvailableEvents] = useState<string[]>([]);
  const [secretKey, setSecretKey] = useState('');
  const { onCopy, hasCopied } = useClipboard(secretKey);
  const toast = useToast();

  // Modal states
  const { isOpen: isCreateOpen, onOpen: onCreateOpen, onClose: onCreateClose } = useDisclosure();
  const { isOpen: isEditOpen, onOpen: onEditOpen, onClose: onEditClose } = useDisclosure();
  const { isOpen: isDeleteOpen, onOpen: onDeleteOpen, onClose: onDeleteClose } = useDisclosure();
  const cancelRef = React.useRef<HTMLButtonElement>(null);

  useEffect(() => {
    fetchWebhooks();
    fetchAvailableEvents();
  }, []);

  const fetchWebhooks = async () => {
    setLoading(true);
    try {
      const response = await webhookService.getAllWebhooks();
      if (response.success && response.data) {
        setWebhooks(response.data);
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to fetch webhook endpoints',
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

  const fetchAvailableEvents = async () => {
    try {
      const response = await webhookService.getAvailableEvents();
      if (response.success && response.data) {
        setAvailableEvents(response.data);
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to fetch available webhook events',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleCreateWebhook = async () => {
    if (!validateWebhookForm(newWebhook)) return;

    try {
      const webhookToCreate = { ...newWebhook };
      if (secretKey) {
        webhookToCreate.secretKey = secretKey;
      }

      const response = await webhookService.createWebhook(webhookToCreate);
      if (response.success && response.data) {
        setWebhooks([response.data, ...webhooks]);
        resetForm();
        onCreateClose();
        toast({
          title: 'Success',
          description: 'Webhook endpoint created successfully',
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to create webhook endpoint',
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

  const handleUpdateWebhook = async () => {
    if (!selectedWebhook || !validateWebhookForm(newWebhook)) return;

    try {
      const webhookToUpdate = { ...newWebhook };
      if (secretKey) {
        webhookToUpdate.secretKey = secretKey;
      }

      const response = await webhookService.updateWebhook(selectedWebhook.id, webhookToUpdate);
      if (response.success && response.data) {
        setWebhooks(webhooks.map(webhook => webhook.id === selectedWebhook.id ? response.data! : webhook));
        resetForm();
        onEditClose();
        toast({
          title: 'Success',
          description: 'Webhook endpoint updated successfully',
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to update webhook endpoint',
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

  const handleDeleteWebhook = async () => {
    if (!selectedWebhook) return;

    try {
      const response = await webhookService.deleteWebhook(selectedWebhook.id);
      if (response.success) {
        setWebhooks(webhooks.filter(webhook => webhook.id !== selectedWebhook.id));
        setSelectedWebhook(null);
        onDeleteClose();
        toast({
          title: 'Success',
          description: 'Webhook endpoint deleted successfully',
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to delete webhook endpoint',
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

  const handleToggleWebhookStatus = async (id: number, enabled: boolean) => {
    try {
      const response = await webhookService.toggleWebhookStatus(id, enabled);
      if (response.success && response.data) {
        setWebhooks(webhooks.map(webhook => webhook.id === id ? response.data! : webhook));
        toast({
          title: 'Success',
          description: `Webhook endpoint ${enabled ? 'enabled' : 'disabled'} successfully`,
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
      } else {
        toast({
          title: 'Error',
          description: response.message || 'Failed to update webhook status',
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

  const openEditModal = (webhook: WebhookEndpoint) => {
    setSelectedWebhook(webhook);
    setNewWebhook({
      name: webhook.name,
      url: webhook.url,
      description: webhook.description || '',
      events: webhook.events,
    });
    setSecretKey(webhook.secretKey || '');
    onEditOpen();
  };

  const validateWebhookForm = (webhook: WebhookEndpointRequest): boolean => {
    if (!webhook.name.trim()) {
      toast({
        title: 'Validation Error',
        description: 'Name is required',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return false;
    }

    if (!webhook.url.trim()) {
      toast({
        title: 'Validation Error',
        description: 'URL is required',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return false;
    }

    try {
      new URL(webhook.url);
    } catch (e) {
      toast({
        title: 'Validation Error',
        description: 'URL is invalid',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return false;
    }

    if (webhook.events.length === 0) {
      toast({
        title: 'Validation Error',
        description: 'At least one event must be selected',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return false;
    }

    return true;
  };

  const resetForm = () => {
    setNewWebhook({
      name: '',
      url: '',
      description: '',
      events: [],
    });
    setSecretKey('');
    setSelectedWebhook(null);
  };

  const generateSecretKey = () => {
    const array = new Uint8Array(32);
    window.crypto.getRandomValues(array);
    const key = Array.from(array, byte => byte.toString(16).padStart(2, '0')).join('');
    setSecretKey(key);
  };

  const formatDate = (dateString: string) => {
    return format(new Date(dateString), 'MMM d, yyyy HH:mm');
  };

  const renderEventBadges = (events: string[]) => {
    if (events.length === 0) return <Text color="gray.500">None</Text>;

    // Show first 3 events, then a count for the rest
    const displayEvents = events.slice(0, 3);
    const remaining = events.length - 3;

    return (
      <Flex flexWrap="wrap" gap={1}>
        {displayEvents.map(event => (
          <Badge key={event} colorScheme="blue" mr={1}>
            {event}
          </Badge>
        ))}
        {remaining > 0 && (
          <Badge colorScheme="gray">+{remaining} more</Badge>
        )}
      </Flex>
    );
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
        <Heading size="md">Webhook Endpoints</Heading>
        <Button leftIcon={<AddIcon />} colorScheme="blue" onClick={onCreateOpen}>
          Create Webhook
        </Button>
      </Flex>

      {webhooks.length === 0 ? (
        <Box p={4} textAlign="center">
          <Text>No webhook endpoints found. Create your first webhook to receive event notifications.</Text>
        </Box>
      ) : (
        <Table variant="simple">
          <Thead>
            <Tr>
              <Th>Name</Th>
              <Th>URL</Th>
              <Th>Events</Th>
              <Th>Status</Th>
              <Th>Actions</Th>
            </Tr>
          </Thead>
          <Tbody>
            {webhooks.map((webhook) => (
              <Tr key={webhook.id}>
                <Td>
                  <Text fontWeight="bold">{webhook.name}</Text>
                  {webhook.description && (
                    <Text fontSize="sm" color="gray.600">
                      {webhook.description}
                    </Text>
                  )}
                </Td>
                <Td>
                  <Tooltip label={webhook.url}>
                    <Text isTruncated maxW="200px">
                      {webhook.url}
                    </Text>
                  </Tooltip>
                </Td>
                <Td>{renderEventBadges(webhook.events)}</Td>
                <Td>
                  <Flex align="center">
                    <Switch
                      isChecked={webhook.enabled}
                      onChange={() => handleToggleWebhookStatus(webhook.id, !webhook.enabled)}
                      mr={2}
                    />
                    <Badge colorScheme={webhook.enabled ? 'green' : 'red'}>
                      {webhook.enabled ? 'Active' : 'Inactive'}
                    </Badge>
                  </Flex>
                </Td>
                <Td>
                  <Flex>
                    <Tooltip label="Edit Webhook">
                      <IconButton
                        aria-label="Edit Webhook"
                        icon={<EditIcon />}
                        size="sm"
                        mr={2}
                        onClick={() => openEditModal(webhook)}
                      />
                    </Tooltip>
                    <Tooltip label="Delete Webhook">
                      <IconButton
                        aria-label="Delete Webhook"
                        icon={<DeleteIcon />}
                        size="sm"
                        colorScheme="red"
                        onClick={() => {
                          setSelectedWebhook(webhook);
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

      {/* Create Webhook Modal */}
      <Modal isOpen={isCreateOpen} onClose={() => { onCreateClose(); resetForm(); }} size="lg">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Create Webhook Endpoint</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <FormControl mb={4} isRequired>
              <FormLabel>Name</FormLabel>
              <Input
                value={newWebhook.name}
                onChange={(e) => setNewWebhook({ ...newWebhook, name: e.target.value })}
                placeholder="e.g., Email Notifications"
              />
            </FormControl>
            <FormControl mb={4} isRequired>
              <FormLabel>URL</FormLabel>
              <Input
                value={newWebhook.url}
                onChange={(e) => setNewWebhook({ ...newWebhook, url: e.target.value })}
                placeholder="https://your-server.com/webhook"
              />
              <FormHelperText>The URL that will receive webhook events</FormHelperText>
            </FormControl>
            <FormControl mb={4}>
              <FormLabel>Description</FormLabel>
              <Textarea
                value={newWebhook.description || ''}
                onChange={(e) => setNewWebhook({ ...newWebhook, description: e.target.value })}
                placeholder="Optional description"
              />
            </FormControl>
            <FormControl mb={4}>
              <FormLabel>Secret Key (Optional)</FormLabel>
              <InputGroup>
                <Input
                  value={secretKey}
                  onChange={(e) => setSecretKey(e.target.value)}
                  placeholder="Secret key for webhook signature"
                  type="text"
                />
                <InputRightElement width="4.5rem">
                  <Button h="1.75rem" size="sm" onClick={generateSecretKey}>
                    Generate
                  </Button>
                </InputRightElement>
              </InputGroup>
              <FormHelperText>
                Used to verify webhook payloads. Keep this secret.
              </FormHelperText>
            </FormControl>
            <FormControl mb={4} isRequired>
              <FormLabel>Events</FormLabel>
              <CheckboxGroup
                colorScheme="blue"
                value={newWebhook.events}
                onChange={(values) => setNewWebhook({ ...newWebhook, events: values as string[] })}
              >
                <Stack direction={['column']} spacing={2}>
                  {availableEvents.map((event) => (
                    <Checkbox key={event} value={event}>
                      {event}
                    </Checkbox>
                  ))}
                </Stack>
              </CheckboxGroup>
              <FormHelperText>Select events to trigger this webhook</FormHelperText>
            </FormControl>
          </ModalBody>
          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={() => { onCreateClose(); resetForm(); }}>
              Cancel
            </Button>
            <Button colorScheme="blue" onClick={handleCreateWebhook}>
              Create
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Edit Webhook Modal */}
      <Modal isOpen={isEditOpen} onClose={() => { onEditClose(); resetForm(); }} size="lg">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Edit Webhook Endpoint</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <FormControl mb={4} isRequired>
              <FormLabel>Name</FormLabel>
              <Input
                value={newWebhook.name}
                onChange={(e) => setNewWebhook({ ...newWebhook, name: e.target.value })}
              />
            </FormControl>
            <FormControl mb={4} isRequired>
              <FormLabel>URL</FormLabel>
              <Input
                value={newWebhook.url}
                onChange={(e) => setNewWebhook({ ...newWebhook, url: e.target.value })}
              />
            </FormControl>
            <FormControl mb={4}>
              <FormLabel>Description</FormLabel>
              <Textarea
                value={newWebhook.description || ''}
                onChange={(e) => setNewWebhook({ ...newWebhook, description: e.target.value })}
              />
            </FormControl>
            <FormControl mb={4}>
              <FormLabel>Secret Key</FormLabel>
              <InputGroup>
                <Input
                  value={secretKey}
                  onChange={(e) => setSecretKey(e.target.value)}
                  placeholder={secretKey ? "••••••••••••••••" : "No secret key set"}
                  type="text"
                />
                <InputRightElement width="4.5rem">
                  <Button h="1.75rem" size="sm" onClick={generateSecretKey}>
                    Generate
                  </Button>
                </InputRightElement>
              </InputGroup>
              <FormHelperText>
                Leave empty to keep the existing secret key
              </FormHelperText>
            </FormControl>
            <FormControl mb={4} isRequired>
              <FormLabel>Events</FormLabel>
              <CheckboxGroup
                colorScheme="blue"
                value={newWebhook.events}
                onChange={(values) => setNewWebhook({ ...newWebhook, events: values as string[] })}
              >
                <Stack direction={['column']} spacing={2}>
                  {availableEvents.map((event) => (
                    <Checkbox key={event} value={event}>
                      {event}
                    </Checkbox>
                  ))}
                </Stack>
              </CheckboxGroup>
            </FormControl>
          </ModalBody>
          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={() => { onEditClose(); resetForm(); }}>
              Cancel
            </Button>
            <Button colorScheme="blue" onClick={handleUpdateWebhook}>
              Update
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Delete Webhook Confirmation Dialog */}
      <AlertDialog isOpen={isDeleteOpen} leastDestructiveRef={cancelRef} onClose={onDeleteClose}>
        <AlertDialogOverlay>
          <AlertDialogContent>
            <AlertDialogHeader fontSize="lg" fontWeight="bold">
              Delete Webhook Endpoint
            </AlertDialogHeader>
            <AlertDialogBody>
              Are you sure you want to delete the webhook endpoint "{selectedWebhook?.name}"? This action cannot be undone.
            </AlertDialogBody>
            <AlertDialogFooter>
              <Button ref={cancelRef} onClick={onDeleteClose}>
                Cancel
              </Button>
              <Button colorScheme="red" onClick={handleDeleteWebhook} ml={3}>
                Delete
              </Button>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialogOverlay>
      </AlertDialog>
    </Box>
  );
};
