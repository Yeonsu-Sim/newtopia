import styled from "styled-components";

export const BackgroundContainer = styled.div`
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
`;

export const BackgroundImage = styled.div`
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  background-image: url('/backgrounds/background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
`;

export const BackgroundGradient = styled.div`
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255,255,255,0.3) 0%, rgba(191,191,191,0.8) 68%, rgba(159,159,159,0.7) 76%, rgba(128,128,128,0.6) 79%, rgba(96,96,96,0.5) 82%, rgba(64,64,64,0.4) 85%, rgba(32,32,32,0.3) 88%, rgba(0,0,0,0.2) 91%);
  opacity: 0.3;
`;

interface BackgroundOverlayProps {
  $variant?: 'default' | 'setup';
}

export const BackgroundOverlay = styled.div<BackgroundOverlayProps>`
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  background: ${({ $variant }) => 
    $variant === 'setup' 
      ? 'rgba(0, 0, 0, 0.7)' 
      : 'rgba(0, 0, 0, 0.3)'
  };
  ${({ $variant }) => 
    $variant === 'setup' && 'backdrop-filter: blur(20px);'
  }
`;