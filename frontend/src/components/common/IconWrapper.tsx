import React from 'react';
import { Icon, IconProps } from '@chakra-ui/react';
import { IconType } from 'react-icons';

interface IconWrapperProps extends Omit<IconProps, 'as'> {
  icon: IconType;
}

export const IconWrapper: React.FC<IconWrapperProps> = ({ icon, ...props }) => {
  return <Icon as={icon as any} {...props} />;
};
