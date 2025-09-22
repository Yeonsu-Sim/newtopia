import styled from 'styled-components'

export const DialogOverlay = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 20;
  cursor: none;
`

export const DialogBox = styled.div`
  display: flex;
  padding: 2rem;
  width: 850px;
  border-radius: 12px;
  text-align: center;
  flex-direction: column;
  gap: 30px;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`

export const DialogText = styled.h2`
  width: 800px;
  margin: 0 auto;
  font-size: 30px;
`

export const ChoiceCards = styled.div`
  display: flex;
  gap: 1rem;
  margin: 1rem auto;
  width: 550px;
`

export const ChoiceCardA = styled.button`
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  transform: rotate(-20deg);
  background-color: #1a95d4;
  color: white;
  font-size: 28px;
  font-weight: bold;
  height: 300px;
  padding: 0px 0px;
  border: 10px solid #33cfff;
  border-radius: 12px;
  cursor: none;
  box-shadow: 6px 6px 0px #004080;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;

  text-shadow: 2px 2px #000080;
  -webkit-text-stroke-width: 0.3px;
  -webkit-text-stroke-color: #28287bff;

  transition: all 0.2s ease;

  &:hover {
    transform: rotate(-20deg) translate(-2px, -2px);
    box-shadow: 6px 6px 0px #004080;
  }

  &:active {
    transform: rotate(-20deg) translate(2px, 2px);
    box-shadow: 2px 2px 0px #004080;
  }
`

export const ChoiceCardB = styled.button`
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  transform: rotate(15deg);
  background-color: #e49000;
  color: white;
  font-size: 28px;
  font-weight: bold;
  height: 300px;
  padding: 0px 0px;
  border: 10px solid #f9bf26;
  border-radius: 12px;
  cursor: none;
  box-shadow: 6px 6px 0px #d57500;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;

  text-shadow: 2px 2px #8e4600;
  -webkit-text-stroke-width: 0.5px;
  -webkit-text-stroke-color: #a24706ff;
  transition: all 0.2s ease;

  &:hover {
    transform: rotate(15deg) translate(-2px, -2px);
    box-shadow: 6px 6px 0px #d57500;
  }

  &:active {
    transform: rotate(15deg) translate(2px, 2px);
    box-shadow: 2px 2px 0px #004080;
  }
`

export const CloseButton = styled.button`
  align-self: flex-end;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: none;
  background: #3498db;
  color: #fff;
  cursor: none;
  font-weight: bold;
  margin-top: 1rem;
  &:hover {
    background: #2980b9;
  }
`

export const CustomCursorImg = styled.img`
  position: fixed;
  pointer-events: none;
  width: 300px;
  height: auto;
  transform: translate(-50%, -50%);
  z-index: 9999;
`
