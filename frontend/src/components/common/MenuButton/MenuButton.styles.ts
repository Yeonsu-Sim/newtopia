import styled from 'styled-components';

interface MenuButtonContainerProps {
  $variant: 'landing' | 'main';
}

interface MenuButtonTextProps {
  $variant: 'landing' | 'main';
}

export const MenuButtonContainer = styled.button<MenuButtonContainerProps>`
  background: #e49000;
  position: relative;
  border-radius: 40px;
  cursor: pointer;
  border: ${props => props.$variant === 'landing' ? '15px solid #f9bf26' : '20px solid #f9bf26'};
  height: ${props => props.$variant === 'landing' ? 'clamp(80px, 15vh, 120px)' : 'clamp(80px, 12vh, 100px)'};
  width: ${props => props.$variant === 'landing' ? '30vw' : '35vw'};
  min-width: ${props => props.$variant === 'landing' ? '250px' : '280px'};
  max-width: ${props => props.$variant === 'landing' ? '400px' : '450px'};
  transition: all 0.2s ease;

  box-shadow: 
    ${props => props.$variant === 'landing' 
      ? 'inset 12px 12px 12px 0px #a35400, 12px 12px 0px #d57500'
      : 'inset 24px 24px 20px 8px #a35400, 12px 12px 0 4px #d57500'
    };

  &:hover {
    transform: scale(1.02);
  }
`;

export const MenuButtonInner = styled.div`
  height: 100%;
  overflow: clip;
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
`;

export const MenuButtonText = styled.div<MenuButtonTextProps>`
  text-align: center;
  white-space: nowrap;
  color: white;
  font-weight: ${props => props.$variant === 'landing' ? 'normal' : 'bold'};
  text-shadow: #6e3400 4px 4px 0px;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: ${props => props.$variant === 'landing' ? 'clamp(18px, 4vw, 42px)' : 'clamp(20px, 4vw, 32px)'};
  line-height: 1;
  transition: color 0.2s ease;
  
  ${MenuButtonContainer}:hover & {
    color: #fef3c7;
  }
`;