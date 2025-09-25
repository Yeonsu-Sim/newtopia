import styled from 'styled-components'

export const ModalOverlay = styled.div`
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
`

export const ModalBackground = styled.div`
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
`

export const ModalContent = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 20px 30px 20px 30px;
  min-width: 90vw;
  max-width: 95vw;
  min-height: 90vh;
  max-height: 95vh;
  border-radius: 40px;
  border: 10px solid #f9bf26;
  background: #e49000;
  box-shadow:
    4px 4px 8px 4px #a35400 inset,
    0px 0px 0 0px #d57500;
  z-index: 1001;

  @media (max-width: 768px) {
    padding: 10px 20px;
    min-width: 95vw;
    max-height: 90vh;
  }
`

export const CloseButton = styled.button`
  position: absolute;
  top: -10px;
  right: -10px;
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

  &:hover {
    background: rgba(0, 0, 0, 0.5);
    transform: scale(1.1);
  }
`

export const ModalHeader = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;

  @media (max-width: 768px) {
    margin-bottom: 20px;
  }
`

export const HeaderTitle = styled.h1`
  background: none;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;

  color: #fff;
  text-align: center;
  text-shadow: 3px 3px 0 #6e3400;
  -webkit-text-stroke-width: 2px;
  -webkit-text-stroke-color: #8e4600;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 42px;
  font-style: normal;
  font-weight: 400;
  line-height: 50px;
  letter-spacing: 2px;

  @media (max-width: 768px) {
    font-size: 36px;
    line-height: 60px;
    letter-spacing: 3px;
    padding: 5px 15px;
  }
`

export const CollectionContainer = styled.div`
  flex: 1;
  overflow-y: auto;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  padding: 20px;

  /* 스크롤바 스타일링 */
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

export const EndingGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 10px;

  @media (max-width: 768px) {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 15px;
  }

  @media (max-width: 480px) {
    grid-template-columns: 1fr;
    gap: 10px;
  }
`

interface EndingCardProps {
  $isUnlocked: boolean;
}

export const EndingCard = styled.div<EndingCardProps>`
  background: ${({ $isUnlocked }) =>
    $isUnlocked ? 'rgba(255, 255, 255, 0.2)' : 'rgba(0, 0, 0, 0.5)'};
  border: 3px solid ${({ $isUnlocked }) =>
    $isUnlocked ? '#f9bf26' : '#666'};
  border-radius: 15px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  transition: all 0.3s ease;
  cursor: ${({ $isUnlocked }) => $isUnlocked ? 'pointer' : 'default'};
  position: relative;
  min-height: 250px;

  ${({ $isUnlocked }) => $isUnlocked && `
    &:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
    }
  `}

  @media (max-width: 768px) {
    padding: 15px;
    min-height: 200px;
  }
`

export const EndingImage = styled.div<EndingCardProps>`
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: ${({ $isUnlocked }) =>
    $isUnlocked ? 'linear-gradient(45deg, #f9bf26, #ffd700)' : '#333'};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60px;
  border: 3px solid ${({ $isUnlocked }) =>
    $isUnlocked ? '#e49000' : '#555'};
  overflow: hidden;
  position: relative;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
  }

  @media (max-width: 768px) {
    width: 100px;
    height: 100px;
    font-size: 50px;
  }

  @media (max-width: 480px) {
    width: 80px;
    height: 80px;
    font-size: 40px;
  }
`

export const EndingTitle = styled.h3<EndingCardProps>`
  color: ${({ $isUnlocked }) => $isUnlocked ? '#fff' : '#888'};
  text-align: center;
  text-shadow: ${({ $isUnlocked }) =>
    $isUnlocked ? '2px 2px 4px #6e3400' : 'none'};
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 24px;
  font-weight: 400;
  line-height: 30px;
  letter-spacing: 1px;
  margin: 0;

  @media (max-width: 768px) {
    font-size: 20px;
    line-height: 26px;
  }

  @media (max-width: 480px) {
    font-size: 18px;
    line-height: 24px;
  }
`

export const EndingDescription = styled.p<EndingCardProps>`
  color: ${({ $isUnlocked }) => $isUnlocked ? '#f0f0f0' : '#666'};
  text-align: center;
  font-family: 'PFStardust', 'Noto Sans KR', sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 22px;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;

  @media (max-width: 768px) {
    font-size: 14px;
    line-height: 20px;
    -webkit-line-clamp: 2;
  }
`

export const LockIcon = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 40px;
  color: #666;
  z-index: 2;

  @media (max-width: 768px) {
    font-size: 32px;
  }

  @media (max-width: 480px) {
    font-size: 28px;
  }
`

export const LoadingSpinner = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #fff;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 32px;
  text-shadow: 4px 4px 0 #6e3400;
`

export const ErrorMessage = styled.div`
  background: rgba(220, 38, 38, 0.8);
  color: white;
  padding: 15px 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  text-align: center;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 24px;
  font-weight: 400;

  @media (max-width: 768px) {
    font-size: 18px;
    padding: 10px 15px;
  }
`