import styled from 'styled-components'

export const ModalOverlay = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  z-Index: 1000;
`

export const ModalBox = styled.div`
  background: #e49000;
  padding: 0rem;
  width: 500px;
  height: 80%;
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

export const ModalHeader = styled.div`
  font-size: 1.8rem;
  padding-top: 10px;
  color: #fdf3d8;
  text-shadow: 2px 2px 0 #6e3400;
  -webkit-text-stroke-width: 1px;
  -webkit-text-stroke-color: #683503;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-weight: 800;
`

export const CloseButton = styled.button`
  position: absolute;
  top: 5px;
  right: 5px;
  width: 30px;
  height: 30px;
  border: none;
  background: rgba(209, 5, 5, 1);
  color: white;
  font-size: 12px;
  font-weight: bold;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-family: 'PFStardustBold', 'Noto Sans KR', sans-serif;

  &:hover {
    background: rgba(0, 0, 0, 0.5);
    transform: scale(1.1);
  }
`

export const NoticeListWrapper = styled.div`
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  &::-webkit-scrollbar {
    width: 12px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.2);
    border-radius: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #f9bf26;
    border-radius: 6px;
    border: 2px solid rgba(0, 0, 0, 0.2);
  }

  &::-webkit-scrollbar-thumb:hover {
    background: #ffd700;
  }
`

export const NoticeItem = styled.div`
  display: flex;
  align-items: center;
  flex-direction: column;
  cursor: pointer;
  padding: 10px 10px;
  margin: 10px;
  border-radius: 8px;
  border: 1px solid #f9bf26;
  background: #ffd966;

  &:hover {
    background: #ffe680;
  }
`

export const NoticeImage = styled.img`
  width: 100%;
  border-radius: 8px;
  margin-bottom: 10px;
`

export const NoticeImageLarge = styled.img`
  width: 100%;
  border-radius: 8px;
  margin-bottom: 10px;
`

export const NoticeTitle = styled.span`
  font-weight: bold;
`

export const NoticeContent = styled.div`
  color: #000;
  white-space: pre-line;
  font-family: 'PFStardustBold', 'Noto Sans KR', sans-serif;
`;

export const NoticeContentSection = styled.div`
  dispaly: flex;
  flex-direction: column;
`;

export const NoticeContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10px;
  margin: 10px;
  height: 100%;
  border-radius: 8px;
  text-align: left;
  background: #fdf3d8;
  overflow-y: auto;
  &::-webkit-scrollbar {
    width: 12px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.2);
    border-radius: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #f9bf26;
    border-radius: 6px;
    border: 2px solid rgba(0, 0, 0, 0.2);
  }

  &::-webkit-scrollbar-thumb:hover {
    background: #ffd700;
  }
`

export const NoticeEmptyWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  margin: 10px;
  height: 100%;
  border-radius: 8px;
  text-align: left;
  background: #fdf3d8;
`;

export const NoticeEmpty = styled.div`
  color: #000;
  font-family: 'PFStardustExtraBold', 'Noto Sans KR', sans-serif;
`;

export const BackButton = styled.button`
  margin-top: 15px;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 5px;
  border: none;
  background: #f9bf26;
  font-weight: bold;
  transition: all 0.2s ease;
  font-family: 'PFStardustExtraBold', 'Noto Sans KR', sans-serif;

  &:hover {
    background: #ffd966;
    transform: scale(1.05);
  }
`