import styled from 'styled-components';

export const Container = styled.div`
  width: 100vw;
  height: 100vh;
  background-color: black;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  flex-direction: column;
  color: white;
  gap: 10px;
`;

export const EndingImage = styled.img<{ fadeIn?: boolean }>`
  width: 400px;
  height: auto;
  object-fit: contain;
  margin-bottom: 2rem;
  opacity: ${({ fadeIn }) => fadeIn ? 1 : 0};
  transition: opacity 1.5s ease-in-out;
`;

export const EndingText = styled.div<{ fadeIn?: boolean }>`
  text-align: center;
  font-size: 1.5rem;
  font-family: 'Galmuri14', 'Noto Sans KR', sans-serif;
  opacity: ${({ fadeIn }) => fadeIn ? 1 : 0};
  transition: opacity 1.5s ease-in-out 0.5s;
`;

export const NextButton = styled.button`
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-family: "PFStardust", 'Noto Sans KR', sans-serif;
  background: #fc7575ff;
  border-radius: 12px;
`;

export const TopRightButton = styled.button`
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-family: "PFStardust", 'Noto Sans KR', sans-serif;
  background: #338fffff;
  border-radius: 12px;
  
`;

export const ButtonGroup = styled.div`
  display: flex;
  gap: 10px;
  flex-direction: column;
  width: 40%;
`;