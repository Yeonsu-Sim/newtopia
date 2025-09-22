import styled from 'styled-components'

export const ModalOverlay = styled.div`
  position: fixed;
  inset: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
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
  z-index: 10;
`

export const ModalInner = styled.div`
  overflow: clip;
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 700px;
`

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
`

export const HeaderTitle = styled.div`
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-weight: 400;
  position: relative;
  padding-top: 10px;
  padding-bottom: 10px;
  flex-shrink: 0;
  color: #fff;
  text-align: center;
  text-shadow: 4px 4px 4px rgba(0, 0, 0, 0.5);
  -webkit-text-stroke-width: 2px;
  -webkit-text-stroke-color: #a35400;
  font-size: 40px;
  font-style: normal;
  line-height: 32px;
  letter-spacing: 4px;

  p {
    line-height: 32px;
    text-wrap: nowrap;
    white-space: pre;
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

  p {
    line-height: 20px;
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
`

export const FormFields = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: start;
  justify-content: start;
  position: relative;
  flex-shrink: 0;
  width: 100%;
  max-width: 500px;
`

export const SelectWrapper = styled.div`
  background: white;
  height: 52px;
  position: relative;
  border-radius: 8px;
  flex-shrink: 0;
  width: 100%;
`

export const Select = styled.select`
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
  background: white;
  cursor: pointer;
  box-sizing: border-box;

  &:focus {
    border-color: #629eff;
  }
`

export const InputWrapper = styled.div`
  background: white;
  height: 52px;
  position: relative;
  border-radius: 8px;
  flex-shrink: 0;
  width: 100%;
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

  &:focus {
    border-color: #629eff;
  }
`

export const TextareaWrapper = styled.div`
  position: relative;
  flex-shrink: 0;
  width: 100%;
`

export const Textarea = styled.textarea`
  width: 100%;
  padding: 1rem;
  border-radius: 8px;
  border: 3px solid #909090;
  outline: none;
  color: black;
  font-size: 16px;
  letter-spacing: 1px;
  font-family: 'Noto Sans KR', sans-serif;
  resize: vertical;
  min-height: 120px;
  box-sizing: border-box;
  background: white;

  &:focus {
    border-color: #629eff;
  }
`

export const FileUploadWrapper = styled.div`
  width: 100%;
`

export const FileUploadButton = styled.div`
  background: white;
  border: 3px solid #909090;
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;

  &:hover {
    border-color: #629eff;
  }

  &:focus-within {
    border-color: #629eff;
  }
`

export const FileUploadText = styled.span`
  color: #666;
  font-family: 'Noto Sans KR', sans-serif;
  font-size: 16px;
  letter-spacing: 1px;
`

export const HiddenFileInput = styled.input`
  display: none;
`

export const FileList = styled.div`
  margin-top: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
`

export const FileItem = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.9);
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: 1px solid #e5e5e5;
`

export const FileItemText = styled.span`
  color: #333;
  font-family: 'Noto Sans KR', sans-serif;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
`

export const FileItemRemove = styled.button`
  color: #dc2626;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  padding: 0.25rem;
  margin-left: 0.5rem;

  &:hover {
    color: #b91c1c;
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

  &:hover:not(:disabled) {
    background: #fcd34d;
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
`
