import styled, { createGlobalStyle } from "styled-components";

export const GameFont = createGlobalStyle`
  @font-face {
    font-family: 'Cafe24ProUp';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/2507-1@1.0/Cafe24PROUP.woff2') format('woff2');
    font-weight: normal;
    font-display: swap;
  }
`;

export const BackgroundWrapper = styled.div`
  position: relative;
  width: 100%;
  height: auto;
  overflow: hidden;
  min-width: 800px;
`;

export const BackgroundImage = styled.img`
  width: 100%;
  height: auto;
  display: block;
`;

export const ParameterWrapper = styled.div<{ x: number; y: number }>`
  position: absolute;
  top: ${({ y }) => y}%;
  left: ${({ x }) => x}%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;

  width: 10%; 
`;

export const ParameterChangeWrapper = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 1rem;
`;

export const MainContainer = styled.div`
  min-height: 100vh;
  padding: 0 3rem 1rem 3rem;
  color: #fff;
  font-family: 'Cafe24ProUp', sans-serif;
  background-color: #1a1a2e;
  min-width: 1000px;
`;

export const Background = styled.img`
  width: 100%;
  height: auto;
  display: block;
`;

export const GameHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 1rem 1rem;
  font-family: 'Cafe24ProUp', sans-serif;
`;

export const TurnBox = styled.div`
  color: #fff;
  font-family: 'Cafe24ProUp', sans-serif;
  text-align: center;
`;

export const InfoBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  background: #2e2e3a;
  padding: 1rem 2rem;
  min-width: 180px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.6);
  color: #fff;
  font-family: 'Cafe24ProUp', sans-serif;
  text-align: center;
  border: 2px solid #555;
`;

export const InfoText = styled.div`
  font-size: 1.2rem;
  margin: 0.3rem 0;
  text-shadow: 1px 1px 2px #000;
`;

export const TurnText = styled.div`
  font-size: 2rem;
  margin: 0.3rem 0;
  text-shadow: 2px 2px 3px #696969ff;
`;

export const ParameterBox = styled.div`
    display: flex;
    align-items: flex-end;
    justify-content: space-around;
    padding-top: 8rem;
    padding-bottom: 8rem;
`;

export const ParameterChangeBox = styled.div`
    display: flex;
    align-items: flex-end;
    justify-content: space-around;
    padding: 2rem;
`;

export const ProgressBar = styled.div`
    width: 100px;
    height: 10px;
    background: #444;
    border-radius: 5px;
    overflow: hidden;
    margin-top: 0.5rem;
    box-shadow: 0 4px 6px rgba(0,0,0,0.6);
`;

export const ProgressFill = styled.div<{ value: number; max: number }>`
    width: ${({ value, max }) => (value / max) * 100}%;
    height: 100%;
    background: ${({ value }) => {
        if (value < 25) return "#e74c3c";
        if (value < 50) return "#f1c40f";
        if (value < 75) return "#2ecc71";
        return "#3498db";
    }};
    transition: width 0.3s ease-in-out, background 0.3s ease-in-out;
    box-shadow: 0 4px 6px rgba(0,0,0,0.6);
`;

export const ProgressBox = styled.div`
    display: flex;
    align-items: baseline;
    gap: 5px;
`;

export const ProgressValue = styled.div`
    color: #ffffff;
    margin: 0.3rem 0;
    font-size: 0.8em;
    text-shadow: 1px 2px 2px #696969ff;
`;

export const ParameterIcon = styled.img<{ type: string; level: number }>`
  width: 100%;
  height: auto;
`;

ParameterIcon.defaultProps = {
  alt: "",
};

export const ParameterEmoji = styled.div`
  font-size: 2rem;
`;

export const GameMessage = styled.div`
    display: flex;
    justify-content: flex-end;
    padding: 1rem;
    text-shadow: 1px 1px 2px #000;
`;

export const MessageBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  background: #2e2e3a;
  padding: 1rem 2rem;
  min-width: 180px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.6);
  color: #fff;
  font-family: 'Cafe24ProUp', sans-serif;
  text-align: center;
  border: 2px solid #555;
  position: relative;
  min-width: 350px;
  cursor: pointer;

  &:hover {
    background: #424242ff;
  }
`;

export const MessageIcon = styled.img.attrs({
    src: "src/assets/icons/새.png",
    alt: "",
})`
    width: 100px;
    height: auto;
    position: absolute;
    top: 50%;
    right: -30px;
    transform: translateY(-50%);
`;