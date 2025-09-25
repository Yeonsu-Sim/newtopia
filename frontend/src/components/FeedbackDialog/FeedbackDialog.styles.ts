import styled from 'styled-components'

export const DialogOverlay = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  z-index: 1000;

`

export const DialogBox = styled.div`
  background: #e49000;
  padding: 0rem;
  width: 700px;
  height: 90%;
  border-radius: 12px;
  text-align: center;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  border: 6px solid #f9bf26;
  box-shadow:
    inset 12px 12px 10px 4px #a35400,
    6px 6px 0px 2px #935100;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`

export const DialogTitle = styled.div`
  font-size: 1.8rem;
  color: #fdf3d8;
  text-shadow: 2px 2px 0 #6e3400;
  -webkit-text-stroke-width: 1px;
  -webkit-text-stroke-color: #683503;
  font-family:
    'PFStardust ExtraBold', 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-weight: 800;
`

export const NewsTitle = styled.div`
  font-size: 1.6rem;
  flex-shrink: 0;
  margin-bottom: 1rem;
  color: #ffe8aaff;
  text-shadow: 2px 2px 0 #6e3400;
  -webkit-text-stroke-width: 1px;
  -webkit-text-stroke-color: #683503;
  font-family:
    'PFStardust ExtraBold', 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-weight: 800;

  a {
    color: inherit;
    text-decoration: underline;
    transition: all 0.2s ease;

    &:hover {
      color: #fcd34d;
      text-decoration: none;
    }
  }
`

export const CommentBox = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
`

export const Comment = styled.div`
  background: #fff;
  color: #2e2e3a;
  padding: 2rem;
  width: 200px;
  border-radius: 12px;
`

export const CloseButton = styled.button`
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  background: linear-gradient(135deg, #f9bf26, #e49000);
  border: 3px solid #8e4600;
  border-radius: 12px;
  padding: 0.5rem 1rem;
  color: #fff;
  text-shadow: 2px 2px 0 #6e3400;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 800;
  font-family:
    'PF Stardust ExtraBold', 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  z-index: 10;
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
`

export const ContentContainer = styled.div`
  margin-top: 0.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
`

export const ContentArea = styled.div`
  flex: 1;
  text-align: left;
  font-size: 0.9rem;
  line-height: 1.6;
  overflow-y: auto;
  padding: 2rem;
  background-color: #fcf8ecff;
  border-radius: 8px;
  border: 3px solid #f9bf26;
  scrollbar-width: thin;
  min-height: 0;
  box-shadow: inset 2px 2px 4px rgba(0, 0, 0, 0.2);
`

export const ContentText = styled.pre`
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  font-family: 'Gmarket Sans TTF', 'Noto Sans KR', sans-serif;
  font-size: 1rem;
  line-height: 1.4;
  color: #2e2e3a;
`
