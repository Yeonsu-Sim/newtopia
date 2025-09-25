import styled from 'styled-components'

export const ModalOverlay = styled.div`
  position: fixed;
  inset: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    padding: 0.5rem;
  }

  @media (max-width: 480px) {
    padding: 0.25rem;
  }
`

export const ModalBackground = styled.div`
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
`

export const ModalContent = styled.div`
  background: #e49000;
  position: relative;
  border-radius: 40px;
  width: 100%;
  max-width: 750px;
  max-height: 95vh;
  overflow-y: auto;
  overflow-x: hidden;
  z-index: 10;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    max-width: 100vw;
    border-radius: 30px;
  }

  @media (max-width: 480px) {
    max-width: 100vw;
    border-radius: 20px;
    max-height: 98vh;
  }
`

export const ModalInner = styled.div`
  overflow: hidden;
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 600px;
  box-sizing: border-box;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    min-height: 550px;
  }

  @media (max-width: 480px) {
    min-height: 500px;
  }
`

export const ModalHeader = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  align-items: center;
  justify-content: center;
  line-height: 0;
  font-style: normal;
  position: relative;
  flex-shrink: 0;
  text-align: center;
  width: 100%;
  padding-top: 10%;
  padding-bottom: 20px;

  /* 모바일에서 패딩 조정 */
  @media (max-width: 768px) {
    padding-top: 12%;
    padding-bottom: 15px;
    gap: 1.25rem;
  }

  @media (max-width: 480px) {
    padding-top: 12%;
    padding-bottom: 10px;
    gap: 1rem;
  }
`

export const HeaderTitle = styled.div`
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-weight: 400;
  position: relative;
  flex-shrink: 0;
  color: #fff;
  text-align: center;
  text-shadow: 4px 4px 4px rgba(0, 0, 0, 0.5);
  -webkit-text-stroke-width: 2px;
  -webkit-text-stroke-color: #a35400;
  font-size: 40px;
  font-style: normal;
  padding-bottom: 10px;
  line-height: 32px;
  letter-spacing: 0px;

  /* 모바일 텍스트 최적화 */
  @media (max-width: 768px) {
    font-size: clamp(24px, 6vw, 36px);
    line-height: 1.2;
    -webkit-text-stroke-width: 1.5px;
  }

  @media (max-width: 480px) {
    font-size: clamp(20px, 7vw, 24px);
    line-height: 1.1;
    -webkit-text-stroke-width: 1px;
    text-shadow: 3px 3px 3px rgba(0, 0, 0, 0.5);
  }

  p {
    line-height: 32px;
    text-wrap: nowrap;
    white-space: pre;

    @media (max-width: 768px) {
      line-height: 1.2;
    }

    @media (max-width: 480px) {
      line-height: 1.1;
    }
  }
`

export const HeaderSubtitle = styled.div`
  font-family: 'PFStardustExtraBold', 'Noto Sans KR', sans-serif;
  font-weight: 800;
  min-width: 100%;
  position: relative;
  padding-bottom: 20px;
  flex-shrink: 0;
  color: #fff;
  text-align: center;
  text-shadow: 4px 4px 4px rgba(0, 0, 0, 0.75);
  font-size: 28px;
  font-style: normal;
  line-height: 20px;
  width: min-content;

  /* 모바일 텍스트 최적화 */
  @media (max-width: 768px) {
    font-size: clamp(20px, 5vw, 24px);
    line-height: 1.2;
    padding-bottom: 15px;
  }

  @media (max-width: 480px) {
    font-size: clamp(16px, 4.5vw, 20px);
    line-height: 1.1;
    padding-bottom: 10px;
    text-shadow: 3px 3px 3px rgba(0, 0, 0, 0.75);
  }

  p {
    line-height: 20px;

    @media (max-width: 768px) {
      line-height: 1.2;
    }

    @media (max-width: 480px) {
      line-height: 1.1;
    }
  }
`

export const ModalForm = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  align-items: center;
  justify-content: start;
  position: relative;
  flex-shrink: 0;
  width: 100%;
  padding: 0 40px 40px 40px;

  /* 모바일 패딩 최적화 */
  @media (max-width: 768px) {
    padding: 0 30px 30px 30px;
    gap: 1.25rem;
  }

  @media (max-width: 480px) {
    padding: 0 20px 20px 20px;
    gap: 1rem;
  }
`

export const ErrorMessage = styled.div`
  width: 100%;
  max-width: 500px;
  padding: 1rem;
  background: #fee2e2;
  border: 1px solid #fca5a5;
  color: #991b1b;
  border-radius: 8px;
  font-family: 'Noto Sans KR', sans-serif;
`

export const FormFields = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  align-items: start;
  justify-content: start;
  position: relative;
  flex-shrink: 0;
  width: 100%;
  max-width: 500px;
`

export const InputWrapper = styled.div`
  background: white;
  height: 52px;
  position: relative;
  border-radius: 8px;
  flex-shrink: 0;
  width: 100%;

  /* 모바일 터치 최적화 */
  @media (max-width: 768px) {
    height: 56px;
  }

  @media (max-width: 480px) {
    height: 60px;
  }
`

export const Input = styled.input`
  width: 100%;
  height: 52px;
  padding: 0 1rem;
  border-radius: 8px;
  border: 3px solid #909090;
  outline: none;
  color: black;
  font-size: 16px;
  letter-spacing: 1px;
  font-family: 'Noto Sans KR', sans-serif;
  box-sizing: border-box;

  /* 모바일 터치 최적화 */
  @media (max-width: 768px) {
    height: 56px;
    font-size: 16px;
    padding: 0 1.25rem;
  }

  @media (max-width: 480px) {
    height: 60px;
    font-size: 16px;
    padding: 0 1rem;
    border-width: 2px;
  }

  &:focus {
    border-color: #629eff;
  }
`

export const SubmitButtonWrapper = styled.div`
  background: #f9bf26;
  position: relative;
  border-radius: 8px;
  flex-shrink: 0;
  width: 100%;
`

export const SubmitButton = styled.button`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  position: relative;
  width: 100%;
  height: 100%;
  border: none;
  background: transparent;
  border-radius: 8px;
  height: 52px;
  cursor: pointer;
  transition: background-color 0.2s;
  touch-action: manipulation;

  /* 모바일 터치 최적화 */
  @media (max-width: 768px) {
    height: 56px;
  }

  @media (max-width: 480px) {
    height: 60px;
  }

  &:hover:not(:disabled) {
    background: #fcd34d;
  }

  /* 터치 디바이스에서 활성화 효과 */
  @media (hover: none) {
    &:hover:not(:disabled) {
      background: transparent;
    }
    
    &:active:not(:disabled) {
      background: #fcd34d;
    }
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`

export const SubmitButtonInner = styled.div`
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  position: relative;
  width: 100%;
`

export const SubmitButtonText = styled.div`
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-weight: normal;
  line-height: 0;
  font-style: normal;
  position: relative;
  flex-shrink: 0;
  font-size: 18px;
  text-wrap: nowrap;
  color: white;
  text-shadow: 2px 2px 2px rgba(0, 0, 0, 0.75);
    -webkit-text-stroke: 1px #a35400; 
  text-stroke: 1px #a35400;

  p {
    line-height: 20px;
    white-space: pre;
  }
`

export const ModeSwitch = styled.div`
  text-align: center;
`

export const ModeSwitchButton = styled.button`
  color: white;
  font-family: 'Noto Sans KR', sans-serif;
  font-size: 16px;
  background: none;
  border: none;
  text-decoration: underline;
  cursor: pointer;
  transition: color 0.2s;

  &:hover {
    color: #fef3c7;
  }
`

export const ModalFrame = styled.div`
  position: absolute;
  inset: 0;
  pointer-events: none;
  box-shadow: 12px 12px 10px 4px inset #a35400;
`

export const ModalBorder = styled.div`
  position: absolute;
  border: 10px solid #f9bf26;
  inset: 0;
  pointer-events: none;
  border-radius: 40px;
  box-shadow: 6px 6px 0px 2px #d57500;
`

export const CloseButton = styled.button`
  position: absolute;
  top: 10px;
  right: 10px;
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(0, 0, 0, 0.3);
  color: white;
  font-size: 24px;
  font-weight: bold;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  touch-action: manipulation;
  z-index: 30;

  /* 모바일 터치 최적화 */
  @media (max-width: 768px) {
    width: 44px;
    height: 44px;
    font-size: 26px;
    top: 8px;
    right: 8px;
  }

  @media (max-width: 480px) {
    width: 48px;
    height: 48px;
    font-size: 28px;
    top: 6px;
    right: 6px;
  }

  &:hover {
    background: rgba(0, 0, 0, 0.5);
    transform: scale(1.1);
  }

  /* 터치 디바이스에서 활성화 효과 */
  @media (hover: none) {
    &:hover {
      background: rgba(0, 0, 0, 0.3);
      transform: none;
    }
    
    &:active {
      background: rgba(0, 0, 0, 0.5);
      transform: scale(0.95);
    }
  }
`
