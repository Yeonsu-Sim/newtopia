import styled from 'styled-components';

interface HeaderTabProps {
  $isActive: boolean;
}

export const ModalOverlay = styled.div`
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
`;

export const ModalBackground = styled.div`
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
`;

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
  border: 10px solid #F9BF26;
  background: #E49000;
  box-shadow: 4px 4px 8px 4px #A35400 inset, 0px 0px 0 0px #D57500;
  z-index: 1001;

  @media (max-width: 768px) {
    padding: 10px 20px;
    min-width: 95vw;
    max-height: 90vh;
  }
`;

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
`;

export const ModalHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 40px;
  margin-bottom: 30px;

  @media (max-width: 768px) {
    gap: 20px;
    margin-bottom: 20px;
    flex-direction: column;
  }
`;

export const HeaderTab = styled.button<HeaderTabProps>`
  background: none;
  border: none;
  cursor: pointer;
  padding: 10px 20px;
  border-radius: 10px;
  transition: all 0.2s ease;

  color: #FFF;
  text-align: center;
  text-shadow: 3px 3px 0 #6E3400;
  -webkit-text-stroke-width: 2px;
  -webkit-text-stroke-color: #8E4600;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 42px;
  font-style: normal;
  font-weight: 400;
  line-height: 50px;
  letter-spacing: 2px;

  ${({ $isActive }) => $isActive && `
    background: rgba(255, 255, 255, 0.2);
  `}

  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }

  @media (max-width: 768px) {
    font-size: 36px;
    line-height: 60px;
    letter-spacing: 3px;
    padding: 5px 15px;
  }
`;

export const SearchSection = styled.div`
  display: flex;
  gap: 15px;
  align-items: center;

  @media (max-width: 768px) {
    flex-direction: column;
    gap: 10px;
  }
`;

interface SearchInputProps {
  $hasError?: boolean;
}

export const SearchInput = styled.input<SearchInputProps>`
  padding: 12px 20px;
  border: 3px solid #6E3400;
  border-radius: 15px;
  background: rgba(255, 255, 255, 0.9);
  font-family: "PFStardustExtraBold", 'Noto Sans KR', sans-serif;
  font-size: 24px;
  font-weight: 400;
  color: #333;
  min-width: 200px;

  &:focus {
    outline: none;
    border-color: #F9BF26;
    background: white;
  }

  &::placeholder {
    color: ${({ $hasError }) => $hasError ? '#dc2626' : '#666'};
  }

  @media (max-width: 768px) {
    font-size: 18px;
    min-width: 150px;
  }
`;

export const SearchButton = styled.button`
  padding: 12px 24px;
  border: 3px solid #6E3400;
  border-radius: 15px;
  background: #F9BF26;
  color: #6E3400;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 24px;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: #FFD700;
    transform: translateY(-2px);
  }

  &:active {
    transform: translateY(0);
  }

  @media (max-width: 768px) {
    font-size: 18px;
    padding: 10px 20px;
  }
`;

export const TableContainer = styled.div`
  flex: 1;
  overflow-y: auto;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  padding: 20px ;

  /* 스크롤바 스타일링 */
  &::-webkit-scrollbar {
    width: 12px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.2);
    border-radius: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #F9BF26;
    border-radius: 6px;
    border: 2px solid rgba(0, 0, 0, 0.2);
  }

  &::-webkit-scrollbar-thumb:hover {
    background: #FFD700;
  }
`;

export const RankingTable = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

export const TableHeader = styled.div`
  display: grid;
  grid-template-columns: 1fr 3fr 2fr 2fr;
  gap: 20px;
  padding: 0px 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  margin-bottom: 10px;

  @media (max-width: 768px) {
    gap: 10px;
    padding: 10px 15px;
  }
`;

export const TableRow = styled.div`
  display: grid;
  grid-template-columns: 1fr 3fr 2fr 2fr;
  gap: 20px;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  transition: all 0.2s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }

  @media (max-width: 768px) {
    gap: 10px;
    padding: 10px 15px;
  }
`;

export const TableCell = styled.div`
  color: #FFFFFF;
  text-align: center;
  text-shadow: 2px 2px 4px #6E3400, -1px -1px 2px #6E3400, 1px -1px 2px #6E3400, -1px 1px 2px #6E3400;
  -webkit-text-stroke-width: 0px;
  -webkit-text-stroke-color: #6E3400;
  // 이거 쉐도우가 왜 피그마랑 반대로 적용되는지 확인필요
  font-family: "PFStardust", 'Noto Sans KR', sans-serif;
  font-size: 32px;
  font-style: normal;
  font-weight: 800;
  line-height: 48px;
  letter-spacing: 2px;

  display: flex;
  align-items: center;
  justify-content: center;
  word-break: break-word;

  @media (max-width: 768px) {
    font-size: 24px;
    line-height: 40px;
    letter-spacing: 2px;
    -webkit-text-stroke-width: 1px;
  }

  @media (max-width: 480px) {
    font-size: 18px;
    line-height: 32px;
    letter-spacing: 1px;
    -webkit-text-stroke-width: 0.5px;
  }
`;

export const LoadingSpinner = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #FFF;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 32px;
  text-shadow: 4px 4px 0 #6E3400;
`;

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
`;