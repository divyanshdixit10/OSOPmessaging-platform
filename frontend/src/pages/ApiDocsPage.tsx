import React from 'react';
import {
  Box,
  Heading,
  Text,
  VStack,
  HStack,
  Accordion,
  AccordionItem,
  AccordionButton,
  AccordionPanel,
  AccordionIcon,
  Badge,
  Code,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Divider,
  Link,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  Alert,
  AlertIcon,
  useColorModeValue,
} from '@chakra-ui/react';

interface Endpoint {
  name: string;
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
  path: string;
  description: string;
  authentication: boolean;
  requestParams?: {
    name: string;
    type: string;
    required: boolean;
    description: string;
  }[];
  requestBody?: {
    [key: string]: {
      type: string;
      required: boolean;
      description: string;
    };
  };
  responseExample: string;
}

const endpoints: Endpoint[] = [
  {
    name: 'List Campaigns',
    method: 'GET',
    path: '/api/campaigns',
    description: 'Retrieves a list of all campaigns for the authenticated user or tenant.',
    authentication: true,
    requestParams: [
      { name: 'page', type: 'integer', required: false, description: 'Page number for pagination' },
      { name: 'size', type: 'integer', required: false, description: 'Number of items per page' },
      { name: 'status', type: 'string', required: false, description: 'Filter by campaign status' },
    ],
    responseExample: `{
  "content": [
    {
      "id": 1,
      "name": "Welcome Campaign",
      "description": "Initial welcome email for new users",
      "status": "COMPLETED",
      "channel": "EMAIL",
      "totalRecipients": 100,
      "createdAt": "2023-06-15T10:30:00Z"
    },
    {
      "id": 2,
      "name": "Product Launch",
      "description": "New product announcement",
      "status": "DRAFT",
      "channel": "EMAIL",
      "totalRecipients": 0,
      "createdAt": "2023-06-20T14:15:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 2,
    "totalPages": 1
  }
}`,
  },
  {
    name: 'Get Campaign',
    method: 'GET',
    path: '/api/campaigns/{id}',
    description: 'Retrieves details of a specific campaign.',
    authentication: true,
    requestParams: [
      { name: 'id', type: 'integer', required: true, description: 'Campaign ID' },
    ],
    responseExample: `{
  "id": 1,
  "name": "Welcome Campaign",
  "description": "Initial welcome email for new users",
  "subject": "Welcome to Our Platform!",
  "body": "<div><h1>Welcome to Our Platform!</h1><p>Dear {{firstName}},</p><p>Thank you for joining our platform. We are excited to have you on board!</p></div>",
  "status": "COMPLETED",
  "channel": "EMAIL",
  "totalRecipients": 100,
  "trackOpens": true,
  "trackClicks": true,
  "addUnsubscribeLink": true,
  "isDraft": false,
  "createdAt": "2023-06-15T10:30:00Z",
  "startedAt": "2023-06-16T08:00:00Z",
  "completedAt": "2023-06-16T08:15:00Z"
}`,
  },
  {
    name: 'Create Campaign',
    method: 'POST',
    path: '/api/campaigns',
    description: 'Creates a new campaign.',
    authentication: true,
    requestBody: {
      name: { type: 'string', required: true, description: 'Campaign name' },
      description: { type: 'string', required: false, description: 'Campaign description' },
      subject: { type: 'string', required: true, description: 'Email subject line' },
      body: { type: 'string', required: true, description: 'Email body content (HTML)' },
      channel: { type: 'string', required: true, description: 'Communication channel (EMAIL, SMS, WHATSAPP)' },
      trackOpens: { type: 'boolean', required: false, description: 'Whether to track email opens' },
      trackClicks: { type: 'boolean', required: false, description: 'Whether to track link clicks' },
      addUnsubscribeLink: { type: 'boolean', required: false, description: 'Whether to add unsubscribe link' },
      isDraft: { type: 'boolean', required: false, description: 'Whether to save as draft' },
    },
    responseExample: `{
  "id": 3,
  "name": "Monthly Newsletter",
  "description": "June 2023 newsletter",
  "subject": "Monthly Newsletter - June 2023",
  "body": "<div><h1>Monthly Newsletter</h1><p>Dear {{firstName}},</p><p>Here are the latest updates for June 2023.</p></div>",
  "status": "DRAFT",
  "channel": "EMAIL",
  "totalRecipients": 0,
  "trackOpens": true,
  "trackClicks": true,
  "addUnsubscribeLink": true,
  "isDraft": true,
  "createdAt": "2023-06-25T09:45:00Z"
}`,
  },
  {
    name: 'Update Campaign',
    method: 'PUT',
    path: '/api/campaigns/{id}',
    description: 'Updates an existing campaign.',
    authentication: true,
    requestParams: [
      { name: 'id', type: 'integer', required: true, description: 'Campaign ID' },
    ],
    requestBody: {
      name: { type: 'string', required: false, description: 'Campaign name' },
      description: { type: 'string', required: false, description: 'Campaign description' },
      subject: { type: 'string', required: false, description: 'Email subject line' },
      body: { type: 'string', required: false, description: 'Email body content (HTML)' },
      trackOpens: { type: 'boolean', required: false, description: 'Whether to track email opens' },
      trackClicks: { type: 'boolean', required: false, description: 'Whether to track link clicks' },
      addUnsubscribeLink: { type: 'boolean', required: false, description: 'Whether to add unsubscribe link' },
      isDraft: { type: 'boolean', required: false, description: 'Whether to save as draft' },
    },
    responseExample: `{
  "id": 3,
  "name": "Monthly Newsletter - Updated",
  "description": "June 2023 newsletter - Updated content",
  "subject": "Monthly Newsletter - June 2023",
  "body": "<div><h1>Monthly Newsletter</h1><p>Dear {{firstName}},</p><p>Here are the latest updates for June 2023.</p></div>",
  "status": "DRAFT",
  "channel": "EMAIL",
  "totalRecipients": 0,
  "trackOpens": true,
  "trackClicks": true,
  "addUnsubscribeLink": true,
  "isDraft": true,
  "createdAt": "2023-06-25T09:45:00Z",
  "updatedAt": "2023-06-25T10:15:00Z"
}`,
  },
  {
    name: 'Delete Campaign',
    method: 'DELETE',
    path: '/api/campaigns/{id}',
    description: 'Deletes a campaign.',
    authentication: true,
    requestParams: [
      { name: 'id', type: 'integer', required: true, description: 'Campaign ID' },
    ],
    responseExample: `{
  "message": "Campaign deleted successfully"
}`,
  },
  {
    name: 'Start Campaign',
    method: 'POST',
    path: '/api/campaigns/{id}/start',
    description: 'Starts a campaign.',
    authentication: true,
    requestParams: [
      { name: 'id', type: 'integer', required: true, description: 'Campaign ID' },
    ],
    responseExample: `{
  "id": 3,
  "name": "Monthly Newsletter",
  "status": "RUNNING",
  "startedAt": "2023-06-26T08:00:00Z"
}`,
  },
  {
    name: 'Get API Keys',
    method: 'GET',
    path: '/api/api-keys',
    description: 'Retrieves a list of API keys for the authenticated user or tenant.',
    authentication: true,
    responseExample: `[
  {
    "id": 1,
    "apiKey": "osop_abcdefg123456",
    "name": "Production API",
    "description": "API key for production environment",
    "enabled": true,
    "createdBy": "admin@example.com",
    "createdAt": "2023-06-15T10:30:00Z"
  },
  {
    "id": 2,
    "apiKey": "osop_hijklmn789012",
    "name": "Development API",
    "description": "API key for development environment",
    "enabled": true,
    "createdBy": "admin@example.com",
    "createdAt": "2023-06-20T14:15:00Z"
  }
]`,
  },
  {
    name: 'Create API Key',
    method: 'POST',
    path: '/api/api-keys',
    description: 'Creates a new API key.',
    authentication: true,
    requestBody: {
      name: { type: 'string', required: true, description: 'API key name' },
      description: { type: 'string', required: false, description: 'API key description' },
      expiresAt: { type: 'string', required: false, description: 'Expiration date (ISO format)' },
    },
    responseExample: `{
  "id": 3,
  "apiKey": "osop_opqrstu345678",
  "name": "Testing API",
  "description": "API key for testing environment",
  "enabled": true,
  "createdBy": "admin@example.com",
  "createdAt": "2023-06-25T09:45:00Z"
}`,
  },
  {
    name: 'Get Webhooks',
    method: 'GET',
    path: '/api/webhooks',
    description: 'Retrieves a list of webhook endpoints for the authenticated user or tenant.',
    authentication: true,
    responseExample: `[
  {
    "id": 1,
    "url": "https://example.com/webhook",
    "name": "Email Notifications",
    "description": "Webhook for email events",
    "events": ["EMAIL_SENT", "EMAIL_OPENED", "EMAIL_CLICKED"],
    "enabled": true,
    "createdAt": "2023-06-15T10:30:00Z"
  },
  {
    "id": 2,
    "url": "https://example.com/webhook/campaign",
    "name": "Campaign Events",
    "description": "Webhook for campaign events",
    "events": ["CAMPAIGN_STARTED", "CAMPAIGN_COMPLETED"],
    "enabled": true,
    "createdAt": "2023-06-20T14:15:00Z"
  }
]`,
  },
  {
    name: 'Create Webhook',
    method: 'POST',
    path: '/api/webhooks',
    description: 'Creates a new webhook endpoint.',
    authentication: true,
    requestBody: {
      url: { type: 'string', required: true, description: 'Webhook URL' },
      name: { type: 'string', required: true, description: 'Webhook name' },
      description: { type: 'string', required: false, description: 'Webhook description' },
      events: { type: 'array', required: true, description: 'Array of event types to trigger this webhook' },
      secretKey: { type: 'string', required: false, description: 'Secret key for webhook signature verification' },
    },
    responseExample: `{
  "id": 3,
  "url": "https://example.com/webhook/analytics",
  "name": "Analytics Events",
  "description": "Webhook for analytics events",
  "events": ["EMAIL_OPENED", "EMAIL_CLICKED"],
  "enabled": true,
  "createdAt": "2023-06-25T09:45:00Z"
}`,
  },
];

export const ApiDocsPage: React.FC = () => {
  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.700');
  const codeBg = useColorModeValue('gray.50', 'gray.700');

  const renderMethodBadge = (method: string) => {
    let colorScheme;
    switch (method) {
      case 'GET':
        colorScheme = 'green';
        break;
      case 'POST':
        colorScheme = 'blue';
        break;
      case 'PUT':
        colorScheme = 'orange';
        break;
      case 'DELETE':
        colorScheme = 'red';
        break;
      case 'PATCH':
        colorScheme = 'purple';
        break;
      default:
        colorScheme = 'gray';
    }
    return <Badge colorScheme={colorScheme}>{method}</Badge>;
  };

  return (
    <Box>
      <VStack align="stretch" spacing={8}>
        <Box>
          <Heading as="h1" size="xl" mb={2}>
            API Documentation
          </Heading>
          <Text color="gray.600">
            This documentation provides information about the OSOP Messaging Platform API endpoints, authentication methods, and examples.
          </Text>
        </Box>

        <Alert status="info">
          <AlertIcon />
          <Box>
            <Text fontWeight="bold">Base URL</Text>
            <Code>http://localhost:8080/api</Code> (Development)
            <br />
            <Code>https://api.osop-messaging.com/api</Code> (Production)
          </Box>
        </Alert>

        <Box>
          <Heading as="h2" size="lg" mb={4}>
            Authentication
          </Heading>
          <Text mb={4}>
            The API supports two authentication methods:
          </Text>

          <VStack align="stretch" spacing={4}>
            <Box p={4} borderWidth="1px" borderRadius="md" bg={bgColor}>
              <Heading as="h3" size="md" mb={2}>
                JWT Authentication
              </Heading>
              <Text mb={2}>
                For user-based authentication, include the JWT token in the Authorization header:
              </Text>
              <Code display="block" p={2} bg={codeBg}>
                Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
              </Code>
              <Text mt={2}>
                JWT tokens can be obtained by calling the <Code>/api/auth/login</Code> endpoint with valid credentials.
              </Text>
            </Box>

            <Box p={4} borderWidth="1px" borderRadius="md" bg={bgColor}>
              <Heading as="h3" size="md" mb={2}>
                API Key Authentication
              </Heading>
              <Text mb={2}>
                For service-to-service authentication, include the API key in the X-API-Key header:
              </Text>
              <Code display="block" p={2} bg={codeBg}>
                X-API-Key: osop_your_api_key_here
              </Code>
              <Text mt={2}>
                API keys can be generated in the Settings page of the application.
              </Text>
            </Box>
          </VStack>
        </Box>

        <Box>
          <Heading as="h2" size="lg" mb={4}>
            Multi-tenancy
          </Heading>
          <Text mb={2}>
            For multi-tenant operations, include the tenant ID in the X-Tenant-ID header:
          </Text>
          <Code display="block" p={2} bg={codeBg} mb={4}>
            X-Tenant-ID: 123
          </Code>
          <Text>
            This header is required for operations that are tenant-specific. If not provided, the tenant will be determined from the authenticated user's context.
          </Text>
        </Box>

        <Box>
          <Heading as="h2" size="lg" mb={4}>
            Endpoints
          </Heading>

          <Accordion allowToggle>
            {endpoints.map((endpoint, index) => (
              <AccordionItem key={index} borderColor={borderColor}>
                <h2>
                  <AccordionButton py={3}>
                    <HStack flex="1" textAlign="left" spacing={3}>
                      {renderMethodBadge(endpoint.method)}
                      <Text fontWeight="bold">{endpoint.name}</Text>
                    </HStack>
                    <AccordionIcon />
                  </AccordionButton>
                </h2>
                <AccordionPanel pb={4}>
                  <VStack align="stretch" spacing={4}>
                    <Box>
                      <Text fontWeight="bold">Endpoint</Text>
                      <Code>{endpoint.path}</Code>
                    </Box>
                    <Box>
                      <Text fontWeight="bold">Description</Text>
                      <Text>{endpoint.description}</Text>
                    </Box>
                    <Box>
                      <Text fontWeight="bold">Authentication Required</Text>
                      <Badge colorScheme={endpoint.authentication ? 'red' : 'green'}>
                        {endpoint.authentication ? 'Yes' : 'No'}
                      </Badge>
                    </Box>

                    {endpoint.requestParams && endpoint.requestParams.length > 0 && (
                      <Box>
                        <Text fontWeight="bold" mb={2}>
                          Request Parameters
                        </Text>
                        <Table size="sm" variant="simple">
                          <Thead>
                            <Tr>
                              <Th>Name</Th>
                              <Th>Type</Th>
                              <Th>Required</Th>
                              <Th>Description</Th>
                            </Tr>
                          </Thead>
                          <Tbody>
                            {endpoint.requestParams.map((param, paramIndex) => (
                              <Tr key={paramIndex}>
                                <Td>{param.name}</Td>
                                <Td>
                                  <Code>{param.type}</Code>
                                </Td>
                                <Td>
                                  <Badge colorScheme={param.required ? 'red' : 'green'}>
                                    {param.required ? 'Yes' : 'No'}
                                  </Badge>
                                </Td>
                                <Td>{param.description}</Td>
                              </Tr>
                            ))}
                          </Tbody>
                        </Table>
                      </Box>
                    )}

                    {endpoint.requestBody && (
                      <Box>
                        <Text fontWeight="bold" mb={2}>
                          Request Body
                        </Text>
                        <Table size="sm" variant="simple">
                          <Thead>
                            <Tr>
                              <Th>Field</Th>
                              <Th>Type</Th>
                              <Th>Required</Th>
                              <Th>Description</Th>
                            </Tr>
                          </Thead>
                          <Tbody>
                            {Object.entries(endpoint.requestBody).map(([field, details], fieldIndex) => (
                              <Tr key={fieldIndex}>
                                <Td>{field}</Td>
                                <Td>
                                  <Code>{details.type}</Code>
                                </Td>
                                <Td>
                                  <Badge colorScheme={details.required ? 'red' : 'green'}>
                                    {details.required ? 'Yes' : 'No'}
                                  </Badge>
                                </Td>
                                <Td>{details.description}</Td>
                              </Tr>
                            ))}
                          </Tbody>
                        </Table>
                      </Box>
                    )}

                    <Box>
                      <Text fontWeight="bold" mb={2}>
                        Response Example
                      </Text>
                      <Box bg={codeBg} p={3} borderRadius="md" overflowX="auto">
                        <pre style={{ whiteSpace: 'pre-wrap' }}>
                          <Code colorScheme="blackAlpha" children={endpoint.responseExample} />
                        </pre>
                      </Box>
                    </Box>
                  </VStack>
                </AccordionPanel>
              </AccordionItem>
            ))}
          </Accordion>
        </Box>

        <Box>
          <Heading as="h2" size="lg" mb={4}>
            Webhooks
          </Heading>
          <Text mb={4}>
            The platform can send webhook notifications for various events. You can configure webhook endpoints in the Settings page.
          </Text>

          <Heading as="h3" size="md" mb={2}>
            Webhook Payload Format
          </Heading>
          <Box bg={codeBg} p={3} borderRadius="md" overflowX="auto" mb={4}>
            <pre style={{ whiteSpace: 'pre-wrap' }}>
              <Code colorScheme="blackAlpha">
                {`{
  "event_type": "EMAIL_OPENED",
  "tenant_id": 123,
  "timestamp": "2023-06-26T10:15:30Z",
  "data": {
    "email_id": 456,
    "campaign_id": 789,
    "recipient": "user@example.com",
    "ip_address": "192.168.1.1",
    "user_agent": "Mozilla/5.0 ..."
  }
}`}
              </Code>
            </pre>
          </Box>

          <Heading as="h3" size="md" mb={2}>
            Webhook Signature Verification
          </Heading>
          <Text mb={2}>
            Webhook payloads are signed with HMAC-SHA256 using the secret key you provide. The signature is included in the X-Webhook-Signature header.
          </Text>
          <Text mb={4}>
            To verify the signature, compute the HMAC-SHA256 of the raw request body using your secret key, and compare it with the value in the X-Webhook-Signature header.
          </Text>

          <Heading as="h3" size="md" mb={2}>
            Available Webhook Events
          </Heading>
          <Table size="sm" variant="simple" mb={4}>
            <Thead>
              <Tr>
                <Th>Event Type</Th>
                <Th>Description</Th>
              </Tr>
            </Thead>
            <Tbody>
              <Tr>
                <Td>EMAIL_SENT</Td>
                <Td>Triggered when an email is sent</Td>
              </Tr>
              <Tr>
                <Td>EMAIL_OPENED</Td>
                <Td>Triggered when a recipient opens an email</Td>
              </Tr>
              <Tr>
                <Td>EMAIL_CLICKED</Td>
                <Td>Triggered when a recipient clicks a link in an email</Td>
              </Tr>
              <Tr>
                <Td>EMAIL_BOUNCED</Td>
                <Td>Triggered when an email bounces</Td>
              </Tr>
              <Tr>
                <Td>EMAIL_UNSUBSCRIBED</Td>
                <Td>Triggered when a recipient unsubscribes</Td>
              </Tr>
              <Tr>
                <Td>CAMPAIGN_STARTED</Td>
                <Td>Triggered when a campaign starts</Td>
              </Tr>
              <Tr>
                <Td>CAMPAIGN_COMPLETED</Td>
                <Td>Triggered when a campaign completes</Td>
              </Tr>
              <Tr>
                <Td>CAMPAIGN_FAILED</Td>
                <Td>Triggered when a campaign fails</Td>
              </Tr>
            </Tbody>
          </Table>
        </Box>

        <Box>
          <Heading as="h2" size="lg" mb={4}>
            Rate Limits
          </Heading>
          <Text mb={2}>
            API rate limits depend on your subscription plan:
          </Text>
          <Table size="sm" variant="simple">
            <Thead>
              <Tr>
                <Th>Plan</Th>
                <Th>Rate Limit</Th>
                <Th>Burst Limit</Th>
              </Tr>
            </Thead>
            <Tbody>
              <Tr>
                <Td>Free</Td>
                <Td>100 requests per minute</Td>
                <Td>200 requests</Td>
              </Tr>
              <Tr>
                <Td>Starter</Td>
                <Td>500 requests per minute</Td>
                <Td>1,000 requests</Td>
              </Tr>
              <Tr>
                <Td>Professional</Td>
                <Td>2,000 requests per minute</Td>
                <Td>5,000 requests</Td>
              </Tr>
              <Tr>
                <Td>Enterprise</Td>
                <Td>10,000 requests per minute</Td>
                <Td>20,000 requests</Td>
              </Tr>
            </Tbody>
          </Table>
        </Box>

        <Box>
          <Heading as="h2" size="lg" mb={4}>
            Error Handling
          </Heading>
          <Text mb={2}>
            The API uses standard HTTP status codes to indicate the success or failure of requests:
          </Text>
          <Table size="sm" variant="simple" mb={4}>
            <Thead>
              <Tr>
                <Th>Status Code</Th>
                <Th>Description</Th>
              </Tr>
            </Thead>
            <Tbody>
              <Tr>
                <Td>200 OK</Td>
                <Td>The request was successful</Td>
              </Tr>
              <Tr>
                <Td>201 Created</Td>
                <Td>The resource was successfully created</Td>
              </Tr>
              <Tr>
                <Td>400 Bad Request</Td>
                <Td>The request was invalid or could not be understood</Td>
              </Tr>
              <Tr>
                <Td>401 Unauthorized</Td>
                <Td>Authentication failed or was not provided</Td>
              </Tr>
              <Tr>
                <Td>403 Forbidden</Td>
                <Td>The authenticated user does not have permission to access the resource</Td>
              </Tr>
              <Tr>
                <Td>404 Not Found</Td>
                <Td>The requested resource was not found</Td>
              </Tr>
              <Tr>
                <Td>429 Too Many Requests</Td>
                <Td>Rate limit exceeded</Td>
              </Tr>
              <Tr>
                <Td>500 Internal Server Error</Td>
                <Td>An error occurred on the server</Td>
              </Tr>
            </Tbody>
          </Table>

          <Heading as="h3" size="md" mb={2}>
            Error Response Format
          </Heading>
          <Box bg={codeBg} p={3} borderRadius="md" overflowX="auto">
            <pre style={{ whiteSpace: 'pre-wrap' }}>
              <Code colorScheme="blackAlpha">
                {`{
  "timestamp": "2023-06-26T10:15:30Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request parameters",
  "path": "/api/campaigns",
  "details": {
    "name": "Name is required",
    "subject": "Subject is required"
  }
}`}
              </Code>
            </pre>
          </Box>
        </Box>

        <Divider my={8} />

        <Box>
          <Text fontSize="sm" color="gray.500">
            For additional support, please contact{' '}
            <Link color="blue.500" href="mailto:support@osop-messaging.com">
              support@osop-messaging.com
            </Link>
          </Text>
        </Box>
      </VStack>
    </Box>
  );
};
