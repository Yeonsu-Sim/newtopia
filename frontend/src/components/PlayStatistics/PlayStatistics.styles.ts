import styled from "styled-components";

interface MetricButtonProps {
  $active: boolean;
  $activeColor?: string;     // 활성화 배경색
  $inactiveColor?: string;   // 비활성화 배경색
  $activeHoverColor?: string;   // 활성화 hover 색
  $inactiveHoverColor?: string; // 비활성화 hover 색
}

interface ChoiceCardProps {
  type: "A" | "B";
  choiceCode: "A" | "B"; // 실제 선택
}

export const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

export const Section = styled.section`
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
`;

export const SectionTitle = styled.h2`
  font-size: 1.4rem;
  margin-bottom: 1rem;
  color: #fff;
  text-align: left;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`;

export const ChartWrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  overflowX: auto;
  width: 100%;
`;

export const Label = styled.div`
  font-size: 20px;
  color: #333;
  font-family: "PFStardustExtraBold", 'Noto Sans KR', sans-serif;
`;

export const SectionHeader = styled.div`
    display: flex;
    justify-content: space-between;
`;

export const ButtonGroup = styled.div`
  display: flex;
  gap: 8px;
`;

export const MetricButton = styled.button<MetricButtonProps>`
  padding: 6px 14px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  background: ${({ $active, $activeColor = "#00796b", $inactiveColor = "#e0e0e0" }) =>
    $active ? $activeColor : $inactiveColor};
  color: ${({ $active }) => ($active ? "white" : "#333")};
  transition: all 0.2s ease;

  &:hover {
    background: ${({ $active, $activeHoverColor = "#00695c", $inactiveHoverColor = "#d5d5d5" }) =>
      $active ? $activeHoverColor : $inactiveHoverColor};
  }
`;

export const TurnNumber = styled.div`
  font-size: 1.2rem;
  color: #fff;
  text-align: left;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`;

export const DetailSection = styled.section`
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
`;

export const DetailHeader = styled.div`
  font-size: 1.2rem;
  color: #000;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`;

export const NpcWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
`;

export const NpcIcon = styled.img`
  width: 300px;
  height: auto;
`;

export const NpcName = styled.div`
  color: #f3f0c7;
  margin: 0 0 0.5rem;
  font-size: 1.4rem;
`;

export const NpcTextBox = styled.div`
  display: flex;
  flex-direction: column;  
  background-color: rgba(44, 62, 80, 0.9);
  padding: 1.5rem 2rem;
  border-radius: 12px 12px 12px 12px;
  align-items: flex-start;
  font-family: "PFStardustBold", 'Noto Sans KR', sans-serif;
`;

export const NpcText = styled.div`
  color: #fff;
  margin: 0 0 1rem;
  font-size: 1.0rem;
`;

export const ChoiceWrapper = styled.div`
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
`;

export const ChoiceCards = styled.div`
  display: flex;
  gap: 1rem;
  margin: 1rem 0;
  align-items: flex-end;
`;

export const ChoiceCard = styled.button<ChoiceCardProps>`
  flex: ${({ type, choiceCode }) => (type === choiceCode ? 1.2 : 0.8)};
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;

  /* 배경색 / 테두리색 */
  background-color: ${({ type, choiceCode }) =>
    type === choiceCode
      ? type === "A" ? "#1a95d4" : "#E49000"
      : "#a0a0a0"}; // 선택 안된 카드는 회색조

  border: 10px solid
    ${({ type, choiceCode }) =>
      type === choiceCode
        ? type === "A" ? "#33cfff" : "#F9BF26"
        : "#888888"}; // 회색 테두리

  box-shadow: 6px 6px 0px
    ${({ type, choiceCode }) =>
      type === choiceCode
        ? type === "A" ? "#004080" : "#D57500"
        : "#666666"}; // 흐린 그림자

  color: ${({ type, choiceCode }) => (type === choiceCode ? "white" : "#dddddd")};
  font-size: ${({ type, choiceCode }) => (type === choiceCode ? "28px" : "22px")};
  font-weight: bold;

  text-shadow: ${({ type, choiceCode }) =>
    type === choiceCode
      ? type === "A" ? "2px 2px #000080" : "2px 2px #8E4600"
      : "1px 1px #555555"}; // 흐린 텍스트 그림자

  height: ${({ type, choiceCode }) => (type === choiceCode ? "350px" : "280px")};
  padding: 20px 40px;
  border-radius: 12px;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  transition: all 0.2s ease;
`;

export const GraphWrapper = styled.section`
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
`;

export const StatsGraphWrapper = styled.div`
  display: flex;
  gap: 24px;
  align-items: flex-end;
  justify-content: space-around;
  color: #000;
  font-family: "PFStardustExtraBold", 'Noto Sans KR', sans-serif;
`;

export const MetricColumn = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  height: 100%;
`;

export const Bars = styled.div`
  display: flex;
  gap: 4px;
  align-items: flex-end;
  height: 100%;
`;

interface BarProps {
  heightPercent: number;
  color: string;
}

export const Bar = styled.div<BarProps>`
  width: 20px;
  height: ${({ heightPercent }) => heightPercent}%;
  background-color: ${({ color }) => color};
  transition: height 0.3s;
  border-radius: 4px 4px 0 0;
`;

export const ArticleWrapper = styled.div`
  display: flex;
  background: #fff;
  flex-direction: column;
  align-items: center;
  border-radius: 12px;
  padding: 1.5rem;
  gap: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
`;

export const ArticleTitle = styled.div`
  color: #494949ff;
  margin: 0 0 0.5rem;
  font-size: 1.4rem;
  font-family: "PFStardustExtraBold", 'Noto Sans KR', sans-serif;
`;

export const ArticleContent = styled.div`
  color: #494949ff;
  margin: 0 0 1rem;
  font-size: 1.0rem;
  font-family: "PFStardustBold", 'Noto Sans KR', sans-serif;
`;

export const ArticleUrl = styled.div`
  color: #2c60ffff;
  margin: 0 0 1rem;
  font-size: 1.2rem;
  font-family: "PFStardustExtraBold", 'Noto Sans KR', sans-serif;
`;