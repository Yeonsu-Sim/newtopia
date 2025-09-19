import styled, { keyframes } from "styled-components";

const slideUpAnimation = keyframes`
  from {
    transform: translateY(100px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
`;

const characterAppearAnimation = keyframes`
  from {
    transform: translateX(-50%) scale(0);
    opacity: 0;
  }
  to {
    transform: translateX(-50%) scale(1);
    opacity: 1;
  }
`;

export const DialogOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  min-height: 100vh;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(20px);
  z-index: 1000;
`;

export const GuestBox = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
`;

export const GuestIcon = styled.img`
  position: absolute;
  left: 50%;
  bottom: clamp(120px, 30vh, 180px);
  transform: translateX(-50%);
  width: clamp(360px, 45vw, 540px);
  height: clamp(540px, 63vh, 810px);
  object-fit: cover;
  z-index: 10;
  animation: ${characterAppearAnimation} 0.8s ease-out;
`;

export const DialogBox = styled.div`
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: clamp(200px, 30vh, 300px);
  background: #e49000;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  padding: clamp(1rem, 4vw, 2.5rem);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  box-shadow:
    inset 6px 6px 5px 2px #a35400,
    3px 3px 0px 1px #935100;
  border: 6px solid #f9bf26;
  z-index: 20;
  animation: ${slideUpAnimation} 0.8s ease-out 0.5s both;

  @media (min-width: 768px) {
    box-shadow:
      inset 12px 12px 10px 4px #a35400,
      6px 6px 0px 2px #935100;
    border: 10px solid #f9bf26;
  }
`;

export const DialogHeader = styled.div`
  position: absolute;
  left: 5%;
  top: -32px;
  background: #ffdfa7;
  border-radius: 12px;
  width: clamp(200px, 40vw, 400px);
  height: clamp(40px, 6vh, 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 1rem;
  color: #FFF;
  text-align: center;
  -webkit-text-stroke-width: 1px;
  -webkit-text-stroke-color: #8E4600;
  font-family: "PFStardust ExtraBold", 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: clamp(22px, 4vw, 32px);
  font-style: normal;
  font-weight: 800;
  line-height: normal;
  border: 4px solid #f9bf26;
  box-shadow: 1px 1px 0px 0px #a7661c;
  z-index: 10;

  @media (min-width: 768px) {
    border: 6px solid #f9bf26;
    box-shadow: 4px 4px 0px 0px #a7661c;
  }
`;

export const DialogContent = styled.div`
  color: #FDF3D8;
  text-shadow: 2px 2px 0 #6E3400;
  -webkit-text-stroke-width: 1px;
  -webkit-text-stroke-color: #683503;
  font-family: "PFStardust ExtraBold", 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: clamp(20px, 5vw, 32px);
  font-style: normal;
  font-weight: 800;
  line-height: normal;
  letter-spacing: clamp(1px, 0.8vw, 1px);
  text-align: center;
  word-break: keep-all;
`;

export const DialogActionsBox = styled.div`
  position: absolute;
  bottom: 0.5rem;
  right: 1rem;
  display: flex;
  gap: 0.5rem;
  z-index: 30;
`;

export const DialogActions = styled.div`
  button {
    background: linear-gradient(135deg, #f9bf26, #e49000);
    border: 3px solid #8E4600;
    color: #FFF;
    text-shadow: 2px 2px 0 #6E3400;
    padding: clamp(0.25rem, 2.5vw, 0.5rem) clamp(1.5rem, 4vw, 2rem);
    border-radius: 12px;
    font-size: clamp(14px, 3vw, 18px);
    font-weight: 800;
    font-family: "PF Stardust ExtraBold", 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
    cursor: pointer;
    transition: all 0.2s ease;
    box-shadow:
      0 4px 8px rgba(0, 0, 0, 0.4),
      inset 2px 2px 4px rgba(255, 255, 255, 0.3);

    &:hover {
      background: linear-gradient(135deg, #fcd34d, #f59e0b);
      transform: translateY(-2px) scale(1.05);
      box-shadow:
        0 6px 12px rgba(0, 0, 0, 0.5),
        inset 2px 2px 4px rgba(255, 255, 255, 0.4);
    }

    &:active {
      transform: translateY(0) scale(1);
      box-shadow:
        0 2px 4px rgba(0, 0, 0, 0.3),
        inset 1px 1px 2px rgba(255, 255, 255, 0.2);
    }

    &:first-child {
      background: linear-gradient(135deg, #d97706, #b45309);
      border: 3px solid #8E4600;

      &:hover {
        background: linear-gradient(135deg, #ea580c, #d97706);
      }
    }
  }
`;