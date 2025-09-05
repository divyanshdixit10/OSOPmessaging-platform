import React, { useState } from 'react';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  Input,
  VStack,
  HStack,
  Text,
  Link,
  Alert,
  AlertIcon,
  Container,
  Heading,
  useColorModeValue,
  Icon,
  InputGroup,
  InputLeftElement,
} from '@chakra-ui/react';
import { FiMail, FiLock, FiEye, FiEyeOff } from 'react-icons/fi';
import { useAuth } from '../contexts/AuthContext';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();
  
  const bgColor = useColorModeValue('gray.50', 'gray.900');
  const cardBg = useColorModeValue('white', 'gray.800');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box minH="100vh" bg={bgColor} display="flex" alignItems="center">
      <Container maxW="md">
        <VStack spacing={8} align="stretch">
          <Box textAlign="center">
            <Heading size="xl" color="brand.500" mb={2}>
              OSOP Messaging
            </Heading>
            <Text color="gray.600">
              Sign in to your account
            </Text>
          </Box>

          <Box bg={cardBg} p={8} borderRadius="lg" shadow="lg">
            <form onSubmit={handleSubmit}>
              <VStack spacing={6}>
                {error && (
                  <Alert status="error" borderRadius="md">
                    <AlertIcon />
                    {error}
                  </Alert>
                )}

                <FormControl isRequired>
                  <FormLabel>Email Address</FormLabel>
                  <InputGroup>
                    <InputLeftElement>
                      <Icon as={FiMail as any} color="gray.400" />
                    </InputLeftElement>
                    <Input
                      type="email"
                      placeholder="Enter your email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                    />
                  </InputGroup>
                </FormControl>

                <FormControl isRequired>
                  <FormLabel>Password</FormLabel>
                  <InputGroup>
                    <InputLeftElement>
                      <Icon as={FiLock as any} color="gray.400" />
                    </InputLeftElement>
                    <Input
                      type={showPassword ? 'text' : 'password'}
                      placeholder="Enter your password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                    />
                    <InputLeftElement right="0">
                      <Button
                        size="sm"
                        variant="ghost"
                        onClick={() => setShowPassword(!showPassword)}
                      >
                        <Icon as={(showPassword ? FiEyeOff : FiEye) as any} />
                      </Button>
                    </InputLeftElement>
                  </InputGroup>
                </FormControl>

                <Button
                  type="submit"
                  colorScheme="brand"
                  size="lg"
                  width="full"
                  isLoading={loading}
                  loadingText="Signing in..."
                >
                  Sign In
                </Button>

                <HStack spacing={1}>
                  <Text color="gray.600">Don't have an account?</Text>
                  <Link as={RouterLink} to="/register" color="brand.500">
                    Sign up
                  </Link>
                </HStack>

                <Link color="brand.500" fontSize="sm">
                  Forgot your password?
                </Link>
              </VStack>
            </form>
          </Box>
        </VStack>
      </Container>
    </Box>
  );
};
