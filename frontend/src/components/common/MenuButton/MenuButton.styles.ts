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
  border-radius: ${props => props.$variant === 'landing' ? '40px' : '30px'};
  cursor: pointer;
  border: ${props => props.$variant === 'landing' ? '15px solid #f9bf26' : '10px solid #f9bf26'};
  height: ${props => props.$variant === 'landing' ? 'clamp(80px, 15vh, 120px)' : 'clamp(90px, 10vh, 100px)'};
  width: ${props => props.$variant === 'landing' ? '30vw' : '28vw'};
  min-width: ${props => props.$variant === 'landing' ? '250px' : '220px'};
  max-width: ${props => props.$variant === 'landing' ? '350px' : '300px'};
  transition: all 0.2s ease;

  box-shadow: inset 12px 12px 12px 0px #a35400, 12px 12px 0px #d57500;

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
  font-weight: normal;
  text-shadow: #6e3400 4px 4px 0px;
  -webkit-text-stroke-width: 2px;
  -webkit-text-stroke-color: #8E4600;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: ${props => props.$variant === 'landing' ? 'clamp(18px, 4vw, 42px)' : 'clamp(16px, 3.5vw, 40px)'};
  line-height: 1;
  transition: color 0.2s ease;

  ${MenuButtonContainer}:hover & {
    color: #fef3c7;
  }
`;