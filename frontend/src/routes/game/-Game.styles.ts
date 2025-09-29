import styled, { createGlobalStyle, keyframes } from 'styled-components'

export const GameFont = createGlobalStyle`
  @font-face {
    font-family: 'Cafe24ProUp';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/2507-1@1.0/Cafe24PROUP.woff2') format('woff2');
    font-weight: normal;
    font-display: swap;
  }
`

export const BackgroundWrapper = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
`

export const BackgroundImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
`

export const ParameterWrapper = styled.div<{ x: number; y: number }>`
  position: absolute;
  top: ${({ y }) => y}%;
  left: ${({ x }) => x}%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  width: 10%;
  min-width: 60px;
  min-height: 60px;
  touch-action: manipulation;

  /* 모바일에서 2x2 그리드 배치 */
  @media (max-width: 768px) {
    &:nth-of-type(1) { /* eco */
      top: 45% !important;
      left: 15% !important;
    }
    &:nth-of-type(2) { /* env */
      top: 45% !important;
      left: 86% !important;
    }
    &:nth-of-type(3) { /* opi */
      top: 55% !important;
      left: 15% !important;
    }
    &:nth-of-type(4) { /* mil */
      top: 55% !important;
      left: 86% !important;
    }
  }

  &:hover {
    .parameter-tooltip {
      opacity: 1;
      visibility: visible;
    }
  }

  /* 터치 디바이스에서 호버 효과 */
  @media (hover: none) {
    &:hover {
      .parameter-tooltip {
        opacity: 0;
        visibility: hidden;
      }
    }
    
    &:active {
      .parameter-tooltip {
        opacity: 1;
        visibility: visible;
      }
    }
  }
`

export const ParameterChangeWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 1rem;
  padding-bottom: 1rem;

  /* 모바일 최적화 - 웹에서 크게, 모바일에서 작게 */
  @media (max-width: 768px) {
    margin: 0.75rem 0.5rem;
  }

  @media (max-width: 480px) {
    margin: 0.5rem 0.25rem;
  }
`

export const MainContainer = styled.div`
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  color: #fff;
  font-family: 'Cafe24ProUp', sans-serif;
  background-color: #1a1a2e;
  
  /* 모바일에서 스크롤 방지 */
  @media (max-width: 768px) {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    height: 100vh;
    height: 100dvh; /* Dynamic viewport height */
    overflow: hidden;
    overscroll-behavior: none;
    touch-action: manipulation;
  }
`

export const Background = styled.img`
  width: 100%;
  height: auto;
  display: block;
`

export const GameHeader = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 2rem;
  font-family: 'DNFBitBitv2', sans-serif;
  z-index: 1;
  pointer-events: none;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    padding: 1rem;
  }

  @media (max-width: 480px) {
    padding: 0.75rem;
  }

  /* InfoBox만 클릭 가능하도록 */
  > * {
    pointer-events: auto;
  }
`

export const TurnBox = styled.div`
  color: #fff;
  font-family: 'DNFBitBitv2', sans-serif;
  text-align: center;
`

export const InfoBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 1rem 2rem;
  min-width: 200px;
  border-radius: 20px;
  border: 5px solid #f9bf26;
  background: #e49000;
  box-shadow:
    2px 2px 4px 2px #a35400 inset,
    0px 0px 0 0px #d57500;
  color: #fff;
  font-family: 'Cafe24ProUp', sans-serif;
  text-align: center;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    padding: 0.75rem 1.5rem;
    min-width: 160px;
    border-radius: 16px;
    border: 4px solid #f9bf26;
  }

  @media (max-width: 480px) {
    padding: 0.5rem 1rem;
    min-width: 120px;
    border-radius: 12px;
    border: 3px solid #f9bf26;
    box-shadow:
      1px 1px 2px 1px #a35400 inset,
      0px 0px 0 0px #d57500;
  }
`

export const InfoText = styled.div`
  font-size: 1.2rem;
  margin: 0.3rem 0;
  color: #ffffff;
  font-family: 'Cafe24ProUp', sans-serif;
  text-shadow:
    2px 2px 4px #6e3400,
    -1px -1px 2px #6e3400,
    1px -1px 2px #6e3400,
    -1px 1px 2px #6e3400;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    font-size: 1rem;
    margin: 0.25rem 0;
  }

  @media (max-width: 480px) {
    font-size: 0.9rem;
    margin: 0.2rem 0;
    text-shadow:
      1px 1px 2px #6e3400,
      -1px -1px 1px #6e3400,
      1px -1px 1px #6e3400,
      -1px 1px 1px #6e3400;
  }
`

export const TurnText = styled.div`
  font-size: 2rem;
  margin: 0.3rem 0;
  text-shadow: 2px 2px 3px #696969ff;
`

export const ParameterBox = styled.div`
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  padding-top: 8rem;
  padding-bottom: 8rem;
`

export const ParameterChangeBox = styled.div`
  display: flex;
  position: relative;
  align-items: flex-end;
  justify-content: space-around;
  padding: 2rem;
  position: relative;
  top: 5%;

  /* 모바일 최적화 - 웹에서 크게, 모바일에서 작게 */
  @media (max-width: 768px) {
    padding: 1.5rem 1rem;
  }

  @media (max-width: 480px) {
    padding: 1rem 0.5rem;
  }
`

export const ProgressBar = styled.div`
  width: 100px;
  height: 10px;
  background: #444;
  border-radius: 5px;
  overflow: hidden;
  margin-top: 0.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.6);
`

export const ProgressFill = styled.div<{ value: number; max: number }>`
  width: ${({ value, max }) => (value / max) * 100}%;
  height: 100%;
  background: ${({ value }) => {
    if (value < 25) return '#e74c3c'
    if (value < 50) return '#f1c40f'
    if (value < 75) return '#2ecc71'
    return '#3498db'
  }};
  transition:
    width 0.3s ease-in-out,
    background 0.3s ease-in-out;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.6);
`

export const ProgressBox = styled.div`
  display: flex;
  align-items: baseline;
  gap: 5px;
  position: relative;
`

export const ProgressValue = styled.div`
  color: #ffffff;
  margin: 0.3rem 0;
  font-size: 0.8em;
  text-shadow: 1px 2px 2px #696969ff;
`

export const ParameterIcon = styled.img<{ $type: string; $level: number }>`
  width: 100%;
  height: auto;
  transition: transform 0.2s ease;

  &:hover {
    transform: scale(1.1);
  }

  /* 힌트 하이라이트 애니메이션 */
  &.highlight-small {
    animation: glowSmall 2s infinite ease-in-out;
  }

  &.highlight-medium {
    animation: glowMedium 2s infinite ease-in-out;
  }

  &.highlight-large {
    animation: glowLarge 2s infinite ease-in-out;
  }

  @keyframes glowSmall {
    0%, 100% {
      filter: drop-shadow(0 0 5px #4CAF50) drop-shadow(0 0 15px #4CAF50) brightness(1.1);
      transform: scale(1);
    }
    50% {
      filter: drop-shadow(0 0 15px #4CAF50) drop-shadow(0 0 25px #4CAF50) brightness(1.3);
      transform: scale(1.08);
    }
  }

  @keyframes glowMedium {
    0%, 100% {
      filter: drop-shadow(0 0 10px #FF9800) drop-shadow(0 0 20px #FF9800) brightness(1.1);
      transform: scale(1);
    }
    50% {
      filter: drop-shadow(0 0 25px #FF9800) drop-shadow(0 0 40px #FF9800) brightness(1.4);
      transform: scale(1.1);
    }
  }

  @keyframes glowLarge {
    0%, 100% {
      filter: drop-shadow(0 0 20px #F44336) drop-shadow(0 0 30px #F44336) brightness(1.2);
      transform: scale(1);
    }
    50% {
      filter: drop-shadow(0 0 35px #F44336) drop-shadow(0 0 55px #F44336) brightness(1.5);
      transform: scale(1.15);
    }
  }
`

ParameterIcon.defaultProps = {
  alt: '',
}

export const ParameterEmoji = styled.div`
  font-size: 2rem;
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  /* 힌트 하이라이트 애니메이션 */
  &.highlight-small {
    animation: emojiGlowSmall 2s infinite ease-in-out;
  }

  &.highlight-medium {
    animation: emojiGlowMedium 2s infinite ease-in-out;
  }

  &.highlight-large {
    animation: emojiGlowLarge 2s infinite ease-in-out;
  }

  @keyframes emojiGlowSmall {
    0%, 100% {
      filter: drop-shadow(0 0 12px #4CAF50) drop-shadow(0 0 20px #4CAF50) brightness(1.2);
      transform: scale(1);
    }
    50% {
      filter: drop-shadow(0 0 20px #4CAF50) drop-shadow(0 0 30px #4CAF50) brightness(1.4);
      transform: scale(1.15);
    }
  }

  @keyframes emojiGlowMedium {
    0%, 100% {
      filter: drop-shadow(0 0 16px #FF9800) drop-shadow(0 0 25px #FF9800) brightness(1.2);
      transform: scale(1);
    }
    50% {
      filter: drop-shadow(0 0 28px #FF9800) drop-shadow(0 0 40px #FF9800) brightness(1.5);
      transform: scale(1.2);
    }
  }

  @keyframes emojiGlowLarge {
    0%, 100% {
      filter: drop-shadow(0 0 20px #F44336) drop-shadow(0 0 35px #F44336) brightness(1.3);
      transform: scale(1);
    }
    50% {
      filter: drop-shadow(0 0 35px #F44336) drop-shadow(0 0 55px #F44336) brightness(1.6);
      transform: scale(1.25);
    }
  }
`

const floatUp = keyframes`
  0% {
    opacity: 1;
    transform: translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateY(-15px);
  }
`

export const ParameterDiff = styled.span<{ $diff: number }>`
  position: absolute;
  top: -1rem;
  left: 80%;
  margin-left: 4px;

  font-weight: bold;
  font-size: 1.5em;
  color: ${({ $diff }) => ($diff > 0 ? '#2ecc71' : $diff < 0 ? '#e74c3c' : 'inherit')};

  animation: ${floatUp} 8.0s ease-out forwards;
  pointer-events: none;
`

export const GameMessage = styled.div`
  display: flex;
  justify-content: flex-end;
  padding: 1rem;
  text-shadow: 1px 1px 2px #000;
`

export const MessageBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  background: #2e2e3a;
  padding: 1rem 2rem;
  min-width: 180px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.6);
  color: #fff;
  font-family: 'Cafe24ProUp', sans-serif;
  text-align: center;
  border: 2px solid #555;
  position: relative;
  min-width: 350px;
  cursor: pointer;

  &:hover {
    background: #424242ff;
  }
`

export const MessageIcon = styled.img.attrs({
  src: 'src/assets/icons/새.png',
  alt: '',
})`
  width: 100px;
  height: auto;
  position: absolute;
  top: 50%;
  right: -30px;
  transform: translateY(-50%);
`

export const EventIcon = styled.img<{ x: number; y: number }>`
  position: absolute;
  top: ${({ y }) => y + 5}%;
  left: ${({ x }) => x}%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  width: 10%;
  min-width: 80px;
  min-height: 80px;
  touch-action: manipulation;
  z-index: 15;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    width: 15%;
    min-width: 100px;
    min-height: 100px;
  }

  @media (max-width: 480px) {
    width: 18%;
    min-width: 120px;
    min-height: 120px;
  }

  &.pop-animation {
    animation: popIn 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  }

  @keyframes popIn {
    0% {
      transform: translate(-50%, -50%) scale(0);
      opacity: 0;
    }
    50% {
      transform: translate(-50%, -50%) scale(1.2);
      opacity: 0.8;
    }
    100% {
      transform: translate(-50%, -50%) scale(1);
      opacity: 1;
    }
  }

  /* 터치 디바이스에서 호버 효과 */
  @media (hover: none) {
    &:active {
      transform: translate(-50%, -50%) scale(0.95);
    }
  }
`

export const LodingIcon = styled.img<{ x: number; y: number }>`
  position: absolute;
  top: ${({ y }) => y}%;
  left: ${({ x }) => x}%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 30%;
  z-index: 15;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    width: 40%;
  }

  @media (max-width: 480px) {
    width: 50%;
  }
`

export const ParameterTooltip = styled.div`
  position: absolute;
  bottom: -45px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-family: 'DNFBitBitv2', sans-serif;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition:
    opacity 0.3s ease,
    visibility 0.3s ease;
  z-index: 10;
  border: 1px solid #555;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);

  &::before {
    content: '';
    position: absolute;
    top: -6px;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 0;
    border-left: 6px solid transparent;
    border-right: 6px solid transparent;
    border-bottom: 6px solid rgba(0, 0, 0, 0.8);
  }
`
export const PauseButton = styled.button`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 1rem 2rem;
  min-width: 200px;
  border-radius: 20px;
  border: 5px solid #ff4d4d; /* 빨간색 테두리 */
  background: #cc0000; /* 빨간색 배경 */
  box-shadow:
    2px 2px 4px 2px #800000 inset,
    0px 0px 0 0px #a80000;
  color: #fff;
  font-family: 'Cafe24ProUp', sans-serif;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease-in-out;

  &:hover {
    background: #e60000;
    transform: scale(1.05);
    box-shadow:
      2px 2px 6px 2px #800000 inset,
      0px 0px 10px 2px rgba(255, 0, 0, 0.7);
  }

  &:active {
    transform: scale(0.95);
    background: #b30000;
  }

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    padding: 0.75rem 1.5rem;
    min-width: 160px;
    border-radius: 16px;
    border: 4px solid #ff4d4d;
  }

  @media (max-width: 480px) {
    padding: 0.5rem 1rem;
    min-width: 120px;
    border-radius: 12px;
    border: 3px solid #ff4d4d;
    box-shadow:
      1px 1px 2px 1px #800000 inset,
      0px 0px 0 0px #a80000;
  }
`

export const TooltipWrapper = styled.div`
  position: absolute;
  bottom: 1.5rem; 
  right: 1.5rem;
  display: inline-block;
  cursor: none;
`

export const TooltipIcon = styled.span`
  display: inline-flex;
  justify-content: center;
  align-items: baseline;
  width: 16px;
  height: 16px;
  font-size: 12px;
  border-radius: 50%;
  background-color: #ccc;
  color: #000;
  font-weight: bold;
  cursor: none;
`

export const TooltipText = styled.div`
  visibility: hidden;
  width: 200px;
  background-color: #333;
  color: #fff;
  text-align: left;
  border-radius: 6px;
  padding: 8px;
  position: absolute;
  z-index: 1;
  top: 125%;
  left: 50%;
  transform: translateX(-90%);
  opacity: 0;
  transition: opacity 0.2s;
  font-family: 'PF Stardust ExtraBold', 'DNFBitBitv2', 'Noto Sans KR', sans-serif;

  ${TooltipWrapper}:hover & {
    visibility: visible;
    opacity: 1;
  }
`