import React from 'react';
import {
  MenuButtonContainer,
  MenuButtonInner,
  MenuButtonText
} from './MenuButton.styles';

export interface MenuButtonProps {
  children: React.ReactNode;
  onClick: () => void;
  variant?: 'landing' | 'main';
  disabled?: boolean;
}

export const MenuButton: React.FC<MenuButtonProps> = ({
  children,
  onClick,
  variant = 'main',
  disabled = false
}) => {
  return (
    <MenuButtonContainer
      onClick={onClick}
      $variant={variant}
      disabled={disabled}
    >
      <MenuButtonInner>
        <MenuButtonText $variant={variant}>
          {children}
        </MenuButtonText>
      </MenuButtonInner>
    </MenuButtonContainer>
  );
};