import styled from 'styled-components'

export const SetupContainer = styled.div`
  position: relative;
  width: 100%;
  height: 100vh;
  min-height: 100vh;
  overflow: hidden;
`

export const TitleSection = styled.div`
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 1152px;
  padding: 0 1rem;
  top: calc(50% - 130px);
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: center;
  justify-content: start;
`

export const TitleText = styled.div`
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-weight: normal;
  line-height: 0;
  font-style: normal;
  position: relative;
  flex-shrink: 0;
  text-align: center;
  white-space: nowrap;
  color: white;
  letter-spacing: -0.25px;
  font-size: clamp(24px, 6vw, 48px);

  p {
    line-height: 1.4;
    white-space: pre;
  }
`

export const FormSection = styled.div`
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  max-width: 599px;
  padding: 0 1rem;
  margin-top: 50px;
`

export const CountryNameForm = styled.form`
  width: 100%;
`

export const InputWrapper = styled.div`
  background: white;
  height: 120px;
  position: relative;
  border-radius: 40px;
  width: 100%;
  border: 15px solid #f9bf26;

  /* 랜딩페이지 MenuButton과 동일한 box-shadow */
  box-shadow:
    inset 12px 12px 12px 0px #a35400,
    12px 12px 0px #d57500;
`

export const InputInner = styled.div`
  height: 100px;
  overflow: clip;
  position: relative;
  width: 100%;
`

export const CountryNameInput = styled.input`
  position: absolute;
  width: 100%;
  height: 100%;
  padding: 0 2rem;
  background: transparent;
  border: none;
  outline: none;
  text-align: center;
  color: #6e3400;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: clamp(24px, 6vw, 48px);
  font-weight: normal;
  text-shadow: #6e3400 2px 2px 0px;
  display: flex;
  align-items: center;
  justify-content: center;
  top: 50%;
  transform: translateY(-50%);

  &::placeholder {
    color: #6e3400;
    opacity: 0.3;
  }
`

export const InstructionText = styled.div`
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);

  p {
    color: #cbd5e1;
    text-align: center;
    font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
    font-size: clamp(14px, 2vw, 18px);
  }
`

export const BackButton = styled.button`
  position: absolute;
  top: 2rem;
  left: 2rem;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 20;
  backdrop-filter: blur(4px);

  &:hover {
    background: rgba(0, 0, 0, 0.7);
    transform: translateY(-1px);
  }
`

export const IntroTextContainer = styled.div`
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 30;

  > div {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 300px;
    text-align: center;
  }
`

export const IntroTextLine = styled.div<{ $isActive: boolean }>`
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-weight: normal;
  color: white;
  letter-spacing: -0.25px;
  font-size: clamp(24px, 6vw, 48px);
  line-height: 1.4;
  margin-bottom: 0.5rem;
  text-shadow: ${({ $isActive }) =>
    $isActive ? '0 0 20px rgba(255, 255, 255, 0.8)' : 'none'};

  &:last-child {
    margin-bottom: 0;
  }
`
