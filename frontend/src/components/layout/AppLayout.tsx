import React, { useState } from 'react';
import {
  Box,
  Flex,
  VStack,
  HStack,
  Text,
  IconButton,
  useColorModeValue,
  useDisclosure,
  Drawer,
  DrawerContent,
  DrawerOverlay,
  DrawerCloseButton,
  DrawerHeader,
  DrawerBody,
  Icon,
  Link,
  Avatar,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  MenuDivider,
  useToast,
} from '@chakra-ui/react';
import {
  FiMenu,
  FiHome,
  FiMail,
  FiFileText,
  FiBarChart,
  FiSettings,
  FiBell,
  FiUser,
  FiLogOut,
  FiChevronDown,
  FiCode,
} from 'react-icons/fi';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { IconType } from 'react-icons';

interface AppLayoutProps {
  children: React.ReactNode;
}

interface NavItem {
  name: string;
  icon: IconType;
  path: string;
}

const navItems: NavItem[] = [
  { name: 'Dashboard', icon: FiHome, path: '/' },
  { name: 'Send Email', icon: FiMail, path: '/send-email' },
  { name: 'Templates', icon: FiFileText, path: '/templates' },
  { name: 'Analytics', icon: FiBarChart, path: '/analytics' },
  { name: 'API Docs', icon: FiCode, path: '/api-docs' },
  { name: 'Settings', icon: FiSettings, path: '/settings' },
];

export const AppLayout: React.FC<AppLayoutProps> = ({ children }) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const location = useLocation();
  const navigate = useNavigate();
  const toast = useToast();
  const { user, logout } = useAuth();
  
  const handleLogout = () => {
    logout();
    navigate('/login');
    toast({
      title: 'Logged out successfully',
      status: 'success',
      duration: 3000,
      isClosable: true,
    });
  };
  
  const sidebarBg = useColorModeValue('white', 'gray.800');
  const sidebarBorderColor = useColorModeValue('gray.200', 'gray.700');
  const headerBg = useColorModeValue('white', 'gray.800');
  const headerBorderColor = useColorModeValue('gray.200', 'gray.700');

  const SidebarContent = () => (
    <VStack spacing={0} align="stretch" h="full">
      <Box p={6} borderBottom="1px solid" borderColor={sidebarBorderColor}>
        <HStack spacing={3}>
          <Box
            w={10}
            h={10}
            bg="brand.500"
            borderRadius="lg"
            display="flex"
            alignItems="center"
            justifyContent="center"
          >
            <Icon as={FiMail as any} color="white" boxSize={5} />
          </Box>
          <Text fontSize="xl" fontWeight="bold" color="brand.500">
            OSOP Messaging
          </Text>
        </HStack>
      </Box>

      <VStack spacing={1} p={4} flex={1}>
        {navItems.map((item) => {
          const isActive = location.pathname === item.path;
          return (
            <Link
              key={item.name}
              w="full"
              p={3}
              borderRadius="lg"
              bg={isActive ? 'brand.50' : 'transparent'}
              color={isActive ? 'brand.700' : 'gray.600'}
              _hover={{
                bg: isActive ? 'brand.100' : 'gray.100',
                textDecoration: 'none',
              }}
              onClick={() => navigate(item.path)}
              cursor="pointer"
              display="flex"
              alignItems="center"
              transition="all 0.2s"
            >
              <Icon as={item.icon as any} mr={3} boxSize={5} />
              <Text fontWeight={isActive ? 'semibold' : 'medium'}>
                {item.name}
              </Text>
            </Link>
          );
        })}
      </VStack>

      <Box p={4} borderTop="1px solid" borderColor={sidebarBorderColor}>
        <HStack spacing={3} p={3} borderRadius="lg" bg="gray.50">
          <Avatar size="sm" name={user?.firstName} src="" />
          <Box flex={1}>
            <Text fontSize="sm" fontWeight="medium" color="gray.700">
              {user?.firstName} {user?.lastName}
            </Text>
            <Text fontSize="xs" color="gray.500">
              {user?.email}
            </Text>
          </Box>
        </HStack>
      </Box>
    </VStack>
  );

  return (
    <Box minH="100vh" bg="gray.50">
      {/* Mobile Sidebar */}
      <Drawer isOpen={isOpen} placement="left" onClose={onClose}>
        <DrawerOverlay />
        <DrawerContent>
          <DrawerCloseButton />
          <DrawerHeader borderBottomWidth="1px">OSOP Messaging</DrawerHeader>
          <DrawerBody p={0}>
            <SidebarContent />
          </DrawerBody>
        </DrawerContent>
      </Drawer>

      {/* Desktop Sidebar */}
      <Box display={{ base: 'none', md: 'block' }}>
        <Box
          w={64}
          h="100vh"
          position="fixed"
          left={0}
          top={0}
          bg={sidebarBg}
          borderRight="1px solid"
          borderColor={sidebarBorderColor}
          zIndex={10}
        >
          <SidebarContent />
        </Box>
      </Box>

      {/* Main Content */}
      <Box ml={{ base: 0, md: 64 }}>
        {/* Header */}
        <Box
          as="header"
          position="sticky"
          top={0}
          zIndex={5}
          bg={headerBg}
          borderBottom="1px solid"
          borderColor={headerBorderColor}
          px={6}
          py={4}
        >
          <Flex justify="space-between" align="center">
            <HStack spacing={4}>
              <IconButton
                display={{ base: 'flex', md: 'none' }}
                onClick={onOpen}
                variant="ghost"
                aria-label="Open menu"
                icon={<Icon as={FiMenu as any} />}
              />
              <Text fontSize="lg" fontWeight="semibold" color="gray.700">
                {navItems.find(item => item.path === location.pathname)?.name || 'Dashboard'}
              </Text>
            </HStack>

            <HStack spacing={4}>
              <IconButton
                variant="ghost"
                aria-label="Notifications"
                icon={<Icon as={FiBell as any} />}
                size="sm"
              />
              
              <Menu>
                <MenuButton
                  as={IconButton}
                  variant="ghost"
                  aria-label="User menu"
                  icon={<Icon as={FiUser as any} />}
                  size="sm"
                />
                <MenuList>
                  <MenuItem icon={<Icon as={FiUser as any} />}>Profile</MenuItem>
                                      <MenuItem icon={<Icon as={FiSettings as any} />}>Settings</MenuItem>
                  <MenuDivider />
                                      <MenuItem icon={<Icon as={FiLogOut as any} />} onClick={handleLogout}>
                    Logout
                  </MenuItem>
                </MenuList>
              </Menu>
            </HStack>
          </Flex>
        </Box>

        {/* Page Content */}
        <Box as="main" p={6}>
          {children}
        </Box>
      </Box>
    </Box>
  );
};
