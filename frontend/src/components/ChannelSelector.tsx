import React from 'react';
import {
  Box,
  RadioGroup,
  Stack,
  Radio,
  useRadioGroup,
  UseRadioGroupProps,
  Center,
  Icon,
} from '@chakra-ui/react';
import { IconType } from 'react-icons';
import { MdEmail, MdPhone, MdWhatsapp } from 'react-icons/md';
import { MessageChannel } from '../types/message';

interface ChannelSelectorProps {
  value: MessageChannel;
  onChange: (value: MessageChannel) => void;
}

const ChannelSelector: React.FC<ChannelSelectorProps> = ({ value, onChange }) => {
  const channelIcons: Record<MessageChannel, IconType> = {
    EMAIL: MdEmail,
    SMS: MdPhone,
    WHATSAPP: MdWhatsapp,
  };

  const channelColors = {
    EMAIL: 'blue.500',
    SMS: 'purple.500',
    WHATSAPP: 'green.500',
  };

  return (
    <RadioGroup value={value} onChange={onChange as any}>
      <Stack direction={{ base: 'column', md: 'row' }} spacing={4}>
        {Object.entries(channelIcons).map(([channel, IconComponent]) => (
          <Box
            key={channel}
            as="label"
            cursor="pointer"
            borderWidth="1px"
            borderRadius="lg"
            p={4}
            transition="all 0.2s"
            _hover={{
              transform: 'translateY(-2px)',
              shadow: 'md',
            }}
            bg={value === channel ? channelColors[channel as MessageChannel] : 'white'}
            color={value === channel ? 'white' : 'gray.600'}
          >
            <Radio value={channel} hidden>
              {channel}
            </Radio>
            <Center flexDirection="column" gap={2}>
              <Icon as={IconComponent as any} fontSize="24px" />
              <Box fontSize="sm" fontWeight="medium">
                {channel}
              </Box>
            </Center>
          </Box>
        ))}
      </Stack>
    </RadioGroup>
  );
};

export default ChannelSelector; 