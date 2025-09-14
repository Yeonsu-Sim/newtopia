import styled from "styled-components";

export const LandingContainer = styled.div`
  position: relative;
  width: 100%;
  height: 100vh;
  min-height: 100vh;
  overflow: hidden;
`;


export const GameLogo = styled.div`
  position: absolute;
  left: 50%;
  top: 15%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
  width: clamp(300px, 40vw, 725px);
  height: clamp(300px, 40vw, 725px);
  aspect-ratio: 1/1;
  background-image: url('/src/assets/icons/CI.png');
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
`;

export const PressStartButton = styled.button`
  position: absolute;
  left: 50%;
  top: 86%;
  transform: translateX(-50%);
  color: white;
  cursor: pointer;
  z-index: 10;
  user-select: none;
  border: none;
  background: none;
  text-shadow: #000000 4px 4px 10px;
  font-family: 'Galmuri14', 'Noto Sans KR', sans-serif;
  font-size: clamp(32px, 8vw, 80px);
  font-weight: normal;
  line-height: 0.8;
  transition: all 0.3s ease;
  
  &:hover {
    color: #fcd34d;
    transform: translateX(-50%) scale(1.05);
  }
`;

export const MenuContainer = styled.div`
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0 1rem;
`;



export const BgmToggleButton = styled.button`
  position: absolute;
  top: 2rem;
  right: 2rem;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.6);
  color: white;bold
  font-size: 24px;
  cursor: pointer;
  z-index: 20;
  backdrop-filter: blur(4px);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:hover {
    background: rgba(0, 0, 0, 0.8);
    transform: scale(1.1);
  }
  
  &:active {
    transform: scale(0.95);
  }
`;