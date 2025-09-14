import styled from "styled-components";

export const ModalOverlay = styled.div`
  position: fixed;
  inset: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
`;

export const ModalBackground = styled.div`
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
`;

export const ModalContent = styled.div`
  background: #e49000;
  position: relative;
  border-radius: 40px;
  width: 100%;
  max-width: 750px;
  max-height: 95vh;
  overflow-y: auto;
  z-index: 10;
`;

export const ModalInner = styled.div`
  overflow: clip;
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 600px;
`;

export const ModalHeader = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  align-items: center;
  justify-content: end;
  line-height: 0;
  font-style: normal;
  position: relative;
  flex-shrink: 0;
  text-align: center;
  width: 100%;
  padding-top: 40px;
  padding-bottom: 20px;
`;

export const HeaderTitle = styled.div`
  font-family: 'Galmuri14', 'Noto Sans KR', sans-serif;
  font-weight: bold;
  position: relative;
  flex-shrink: 0;
  color: white;
  letter-spacing: 2px;
  text-shadow: rgba(0, 0, 0, 0.5) 4px 4px 4px;
  font-size: clamp(24px, 5vw, 32px);
  
  p {
    line-height: 1.2;
    text-wrap: nowrap;
    white-space: pre;
  }
`;

export const HeaderSubtitle = styled.div`
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  min-width: 100%;
  position: relative;
  flex-shrink: 0;
  color: rgba(0, 0, 0, 0.8);
  font-size: clamp(16px, 3vw, 20px);
  width: min-content;
  
  p {
    line-height: 1.3;
  }
`;

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
`;

export const ErrorMessage = styled.div`
  width: 100%;
  max-width: 500px;
  padding: 1rem;
  background: #fee2e2;
  border: 1px solid #fca5a5;
  color: #991b1b;
  border-radius: 8px;
  font-family: 'Noto Sans KR', sans-serif;
`;

export const FormFields = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: start;
  justify-content: start;
  position: relative;
  flex-shrink: 0;
  width: 100%;
  max-width: 500px;
`;

export const InputWrapper = styled.div`
  background: white;
  height: 52px;
  position: relative;
  border-radius: 8px;
  flex-shrink: 0;
  width: 100%;
`;

export const Input = styled.input`
  width: 100%;
  height: 52px;
  padding: 0 1rem;
  border-radius: 8px;
  border: 3px solid #629eff;
  outline: none;
  color: black;
  font-size: 16px;
  letter-spacing: 1px;
  font-family: 'Noto Sans KR', sans-serif;
  
  &:focus {
    border-color: #3b82f6;
  }
  
  &[type="password"] {
    border-color: #909090;
    
    &:focus {
      border-color: #3b82f6;
    }
  }
`;

export const SubmitButtonWrapper = styled.div`
  background: #f9bf26;
  position: relative;
  border-radius: 8px;
  flex-shrink: 0;
  width: 100%;
`;

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
  
  &:hover:not(:disabled) {
    background: #fcd34d;
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

export const SubmitButtonInner = styled.div`
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  position: relative;
  width: 100%;
`;

export const SubmitButtonText = styled.div`
  font-family: 'Galmuri14', 'Noto Sans KR', sans-serif;
  font-weight: bold;
  line-height: 0;
  font-style: normal;
  position: relative;
  flex-shrink: 0;
  font-size: 18px;
  text-wrap: nowrap;
  color: white;
  
  p {
    line-height: 20px;
    white-space: pre;
  }
`;

export const ModeSwitch = styled.div`
  text-align: center;
`;

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
`;

export const ModalFrame = styled.div`
  position: absolute;
  inset: 0;
  pointer-events: none;
  box-shadow: 12px 12px 10px 4px inset #a35400;
`;

export const ModalBorder = styled.div`
  position: absolute;
  border: 10px solid #f9bf26;
  inset: 0;
  pointer-events: none;
  border-radius: 40px;
  box-shadow: 6px 6px 0px 2px #d57500;
`;

export const CloseButton = styled.button`
  position: absolute;
  top: 1rem;
  right: 1rem;
  color: white;
  font-size: 1.25rem;
  z-index: 20;
  background: #dc2626;
  border: none;
  border-radius: 50%;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  cursor: pointer;
  
  &:hover {
    color: #fca5a5;
    background: #b91c1c;
  }
`;