import styled from 'styled-components'

interface MenuButtonContainerProps {
  $variant: 'landing' | 'main'
}

interface MenuButtonTextProps {
  $variant: 'landing' | 'main'
}

export const MenuButtonContainer = styled.button<MenuButtonContainerProps>`
  background: #e49000;
  position: relative;
  border-radius: ${(props) => (props.$variant === 'landing' ? '40px' : '30px')};
  cursor: pointer;
  border: ${(props) =>
    props.$variant === 'landing' ? '15px solid #f9bf26' : '10px solid #f9bf26'};
  height: ${(props) =>
    props.$variant === 'landing'
      ? 'clamp(80px, 15vh, 120px)'
      : 'clamp(90px, 10vh, 100px)'};
  width: ${(props) => (props.$variant === 'landing' ? '30vw' : '28vw')};
  min-width: ${(props) => (props.$variant === 'landing' ? '250px' : '220px')};
  max-width: ${(props) => (props.$variant === 'landing' ? '350px' : '300px')};
  transition: all 0.2s ease;
  touch-action: manipulation;

  box-shadow:
    inset 12px 12px 12px 0px #a35400,
    12px 12px 0px #d57500;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    width: ${(props) => (props.$variant === 'landing' ? '80vw' : '75vw')};
    min-width: ${(props) => (props.$variant === 'landing' ? '280px' : '250px')};
    max-width: ${(props) => (props.$variant === 'landing' ? '320px' : '290px')};
    height: ${(props) =>
      props.$variant === 'landing'
        ? 'clamp(70px, 12vh, 100px)'
        : 'clamp(60px, 8vh, 80px)'};
    border-width: ${(props) =>
      props.$variant === 'landing' ? '12px' : '8px'};
    border-radius: ${(props) => (props.$variant === 'landing' ? '32px' : '24px')};
    
    box-shadow:
      inset 8px 8px 8px 0px #a35400,
      8px 8px 0px #d57500;
  }

  @media (max-width: 480px) {
    width: ${(props) => (props.$variant === 'landing' ? '85vw' : '80vw')};
    min-width: ${(props) => (props.$variant === 'landing' ? '260px' : '230px')};
    max-width: ${(props) => (props.$variant === 'landing' ? '280px' : '250px')};
    height: ${(props) =>
      props.$variant === 'landing'
        ? 'clamp(60px, 10vh, 80px)'
        : 'clamp(50px, 7vh, 70px)'};
    border-width: ${(props) =>
      props.$variant === 'landing' ? '10px' : '6px'};
    border-radius: ${(props) => (props.$variant === 'landing' ? '28px' : '20px')};
    
    box-shadow:
      inset 6px 6px 6px 0px #a35400,
      6px 6px 0px #d57500;
  }

  &:hover {
    transform: scale(1.02);
  }

  /* 터치 디바이스에서 호버 효과 비활성화 */
  @media (hover: none) {
    &:hover {
      transform: none;
    }
    
    &:active {
      transform: scale(0.98);
      background: #d18000;
    }
  }
`

export const MenuButtonInner = styled.div`
  height: 100%;
  overflow: clip;
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
`

export const MenuButtonText = styled.div<MenuButtonTextProps>`
  text-align: center;
  white-space: nowrap;
  color: white;
  font-weight: normal;
  text-shadow: #6e3400 4px 4px 0px;
  -webkit-text-stroke-width: 2px;
  -webkit-text-stroke-color: #8e4600;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: ${(props) =>
    props.$variant === 'landing'
      ? 'clamp(18px, 4vw, 42px)'
      : 'clamp(16px, 3.5vw, 40px)'};
  line-height: 1;
  transition: color 0.2s ease;

  /* 모바일 텍스트 최적화 */
  @media (max-width: 768px) {
    font-size: ${(props) =>
      props.$variant === 'landing'
        ? 'clamp(20px, 5vw, 32px)'
        : 'clamp(18px, 4.5vw, 28px)'};
    text-shadow: #6e3400 3px 3px 0px;
    -webkit-text-stroke-width: 1.5px;
  }

  @media (max-width: 480px) {
    font-size: ${(props) =>
      props.$variant === 'landing'
        ? 'clamp(18px, 5.5vw, 26px)'
        : 'clamp(16px, 5vw, 22px)'};
    text-shadow: #6e3400 2px 2px 0px;
    -webkit-text-stroke-width: 1px;
    white-space: normal;
    word-break: keep-all;
    line-height: 1.1;
  }

  ${MenuButtonContainer}:hover & {
    color: #fef3c7;
  }

  /* 터치 디바이스에서 활성화 상태 색상 */
  @media (hover: none) {
    ${MenuButtonContainer}:hover & {
      color: white;
    }
    
    ${MenuButtonContainer}:active & {
      color: #fef3c7;
    }
  }
`
