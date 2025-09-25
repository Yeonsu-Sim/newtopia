import styled, { keyframes } from 'styled-components'

interface NewsItemProps {
  $isVisible: boolean
  $index: number
  $currentIndex: number
  $totalItems: number
}

// 위에서 아래로 슬라이드되는 애니메이션
const slideInFromTop = keyframes`
  from {
    transform: translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
`

// 아래로 슬라이드되어 나가는 애니메이션
const slideOutToBottom = keyframes`
  from {
    transform: translateY(0);
    opacity: 1;
  }
  to {
    transform: translateY(100%);
    opacity: 0;
  }
`

export const HotTopicContainer = styled.div`
  display: flex;
  width: 100%;
  max-width: 1920px;
  padding: 0px 40px;
  justify-content: center;
  align-items: center;
  background: rgba(34, 41, 59, 0.8);
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  z-index: 5;
  min-height: 40px;

  @media (max-width: 1920px) {
    width: 100vw;
    padding: 10px 20px;
  }

  @media (max-width: 768px) {
    padding: 12px 16px;
    min-height: 50px;
  }

  @media (max-width: 480px) {
    padding: 8px 12px;
    min-height: 45px;
  }
`

export const ContentWrapper = styled.div`
  display: flex;
  width: 100%;
  align-items: center;
  gap: 20px;

  @media (max-width: 768px) {
    gap: 8px;
    flex-direction: row;
    align-items: flex-start;
    justify-content: flex-start;
  }

  @media (max-width: 480px) {
    gap: 6px;
  }
`

export const HotTopicTitle = styled.h2`
  color: #fff;
  font-family: 'GmarketSansTTF', 'NotoSans KR', sans-serif;
  font-size: 18px;
  font-style: normal;
  font-weight: 500;
  line-height: normal;
  margin: 0;
  white-space: nowrap;
  flex-shrink: 0;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);

  @media (max-width: 768px) {
    display: none;
  }

  @media (max-width: 480px) {
    display: none;
  }
`

export const NewsScrollContainer = styled.div`
  position: relative;
  flex: 1;
  height: 24px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: flex-start;

  @media (max-width: 768px) {
    height: 26px;
    justify-content: flex-start;
  }

  @media (max-width: 480px) {
    height: 22px;
  }
`

export const NewsItem = styled.div<NewsItemProps>`
  position: absolute;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;

  /* 애니메이션 적용 */
  animation: ${(props) =>
      props.$isVisible ? slideInFromTop : slideOutToBottom}
    0.5s ease-in-out;

  /* 현재 보이는 아이템만 표시 */
  opacity: ${(props) => (props.$isVisible ? 1 : 0)};
  transform: ${(props) =>
    props.$isVisible
      ? 'translateY(0)'
      : props.$index < props.$currentIndex
        ? 'translateY(100%)'
        : 'translateY(-100%)'};

  transition: all 0.5s ease-in-out;

  @media (max-width: 768px) {
    justify-content: center;
  }
`

export const NewsLink = styled.button`
  background: none;
  border: none;
  color: #fff;
  font-family: 'Galmuri14', 'Noto Sans KR', sans-serif;
  font-size: 18px;
  font-style: normal;
  line-height: 1.2;
  letter-spacing: 1px;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 0;
  transition: color 0.2s ease;
  max-width: 100%;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);

  &:hover {
    color: #fef3c7;
  }

  @media (max-width: 768px) {
    font-size: 15px;
    letter-spacing: 0px;
    text-align: left;
  }

  @media (max-width: 480px) {
    font-size: 15px;
    letter-spacing: 0px;
    line-height: 1.1;
  }
`

export const NewsIcon = styled.span`
  margin-right: 8px;
  flex-shrink: 0;

  @media (max-width: 480px) {
    margin-right: 4px;
  }
`
