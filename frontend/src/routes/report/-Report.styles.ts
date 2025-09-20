import styled from "styled-components";

export const Container = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  background: #000;
  padding: 2rem;
  font-family: "NeoDunggeunmo", sans-serif;

  min-height: 100vh;
  min-width: 700px;
  box-sizing: border-box;
`;

export const MainContainer = styled.div`
  display: flex;
  flex-direction: column;
  background: #2e2e3a;
  padding: 1rem 2rem;
  width: 700px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.6);
  color: #fff;
  text-align: center;
  border: 2px solid #555;
`;

export const Header = styled.h1`
  font-size: 1.8rem;
  font-weight: normal;
  margin-bottom: 1.5rem;
  text-align: center;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`;

export const ScoreSection = styled.div`
  display: flex;
  justify-content: space-around;
  margin-bottom: 2rem;
`;

export const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
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
  color: #00796b;
  text-align: left;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`;

export const ChartWrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const TopRightButton = styled.button`
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-family: "PFStardust", 'Noto Sans KR', sans-serif;
  background: #338fffff;
  border-radius: 12px;
`;