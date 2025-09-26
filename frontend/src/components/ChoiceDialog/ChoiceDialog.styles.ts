import styled from 'styled-components'

export const DialogOverlay = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 20;
  cursor: none;
  padding: 1rem;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    padding: 0.5rem;
  }

  @media (max-width: 480px) {
    padding: 0.25rem;
  }
`

export const DialogBox = styled.div`
  display: flex;
  padding: 3rem;
  width: 100%;
  max-width: 1200px;
  min-width: 900px;
  border-radius: 16px;
  text-align: center;
  flex-direction: column;
  gap: 40px;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    padding: 1.5rem;
    gap: 20px;
    max-width: 95vw;
    min-width: unset;
    border-radius: 12px;
  }

  @media (max-width: 480px) {
    padding: 1rem;
    gap: 15px;
    max-width: 98vw;
  }
`

export const DialogText = styled.h2`
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  font-size: 36px;
  line-height: 1.4;
  word-break: keep-all;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    font-size: 24px;
    max-width: 100%;
  }

  @media (max-width: 480px) {
    font-size: 20px;
    line-height: 1.3;
  }
`

export const ChoiceCards = styled.div`
  display: flex;
  gap: 3rem;
  margin: 2rem auto;
  width: 100%;
  max-width: 800px;
  justify-content: center;
  align-items: center;

  /* 모바일 최적화 - 교차 배치 유지 */
  @media (max-width: 768px) {
    flex-direction: row;
    gap: 2rem;
    max-width: 500px;
    margin: 1rem auto;
  }

  @media (max-width: 480px) {
    gap: 1.5rem;
    max-width: 400px;
  }
`

export const ChoiceCardA = styled.button`
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  transform: rotate(-15deg);
  background-color: #1a95d4;
  color: white;
  font-size: 32px;
  font-weight: bold;
  height: 400px;
  width: 280px;
  max-width: 280px;
  padding: 1.5rem;
  border: 12px solid #33cfff;
  border-radius: 16px;
  cursor: none;
  box-shadow: 8px 8px 0px #004080;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  touch-action: manipulation;
  aspect-ratio: 7/10;
  word-break: keep-all;
  line-height: 1.2;

  text-shadow: 3px 3px #000080;
  -webkit-text-stroke-width: 0.5px;
  -webkit-text-stroke-color: #28287bff;

  transition: all 0.2s ease;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    transform: rotate(-10deg);
    height: 270px;
    width: 180px;
    max-width: 180px;
    font-size: 21px;
    border: 5px solid #33cfff;
    aspect-ratio: 2/3;
    padding: 0.75rem;
    
    &:hover {
      transform: rotate(-10deg) translate(-2px, -2px);
    }
    
    &:active {
      transform: rotate(-10deg) translate(2px, 2px);
    }
  }

  @media (max-width: 480px) {
    transform: rotate(-8deg);
    height: 225px;
    width: 150px;
    max-width: 150px;
    font-size: 17px;
    border: 4px solid #33cfff;
    aspect-ratio: 2/3;
    padding: 0.5rem;
    
    &:hover {
      transform: rotate(-8deg) translate(-1px, -1px);
      box-shadow: 4px 4px 0px #004080;
    }
    
    &:active {
      transform: rotate(-8deg) translate(1px, 1px);
      box-shadow: 2px 2px 0px #004080;
    }
  }

  &:hover {
    transform: rotate(-20deg) translate(-4px, -4px);
    box-shadow: 12px 12px 0px #004080;
  }

  &:active {
    transform: rotate(-20deg) translate(4px, 4px);
    box-shadow: 4px 4px 0px #004080;
  }
`

export const ChoiceCardB = styled.button`
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  transform: rotate(12deg);
  background-color: #e49000;
  color: white;
  font-size: 32px;
  font-weight: bold;
  height: 400px;
  width: 280px;
  max-width: 280px;
  padding: 1.5rem;
  border: 12px solid #f9bf26;
  border-radius: 16px;
  cursor: none;
  box-shadow: 8px 8px 0px #d57500;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  touch-action: manipulation;
  aspect-ratio: 7/10;
  word-break: keep-all;
  line-height: 1.2;

  text-shadow: 3px 3px #8e4600;
  -webkit-text-stroke-width: 0.7px;
  -webkit-text-stroke-color: #a24706ff;

  /* 모바일 최적화 */
  @media (max-width: 768px) {
    transform: rotate(10deg);
    height: 270px;
    width: 180px;
    max-width: 180px;
    font-size: 21px;
    border: 5px solid #f9bf26;
    aspect-ratio: 2/3;
    padding: 0.75rem;
    
    &:hover {
      transform: rotate(10deg) translate(-2px, -2px);
    }
    
    &:active {
      transform: rotate(10deg) translate(2px, 2px);
    }
  }

  @media (max-width: 480px) {
    transform: rotate(8deg);
    height: 225px;
    width: 150px;
    max-width: 150px;
    font-size: 17px;
    border: 4px solid #f9bf26;
    aspect-ratio: 2/3;
    padding: 0.5rem;
    
    &:hover {
      transform: rotate(8deg) translate(-1px, -1px);
      box-shadow: 4px 4px 0px #d57500;
    }
    
    &:active {
      transform: rotate(8deg) translate(1px, 1px);
      box-shadow: 2px 2px 0px #d57500;
    }
  }
  transition: all 0.2s ease;

  &:hover {
    transform: rotate(15deg) translate(-4px, -4px);
    box-shadow: 12px 12px 0px #d57500;
  }

  &:active {
    transform: rotate(15deg) translate(4px, 4px);
    box-shadow: 4px 4px 0px #d57500;
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

