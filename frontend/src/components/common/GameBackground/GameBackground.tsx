import React from 'react';
import {
  BackgroundContainer,
  BackgroundImage,
  BackgroundGradient,
  BackgroundOverlay
} from './GameBackground.styles';

interface GameBackgroundProps {
  /**
   * 게임 셋업 페이지에서는 더 강한 블러와 어두운 오버레이 적용
   */
  variant?: 'default' | 'setup';
}

export const GameBackground: React.FC<GameBackgroundProps> = ({ 
  variant = 'default' 
}) => {
  return (
    <BackgroundContainer>
      <BackgroundImage />
      <BackgroundGradient />
      <BackgroundOverlay $variant={variant} />
    </BackgroundContainer>
  );
};