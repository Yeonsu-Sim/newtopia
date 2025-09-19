import styled from "styled-components";

export const TestContainer = styled.div`
  position: relative;
  min-height: 100vh;
  background: #1a1a2e;
  color: #fff;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`;

export const TestHeader = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 2000;
  background: rgba(0, 0, 0, 0.9);
  backdrop-filter: blur(8px);
  padding: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
`;

export const TestTitle = styled.h1`
  font-size: 1.5rem;
  margin: 0 0 1rem 0;
  text-align: center;
  color: #fff;
`;

export const TestControls = styled.div`
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
`;

export const TestButton = styled.button<{ variant?: 'reset' }>`
  background: ${({ variant }) => variant === 'reset' ? '#dc2626' : '#3b82f6'};
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: ${({ variant }) => variant === 'reset' ? '#b91c1c' : '#2563eb'};
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }
`;

export const MockGameContainer = styled.div`
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  margin-top: 120px;
`;

export const MockBackgroundImage = styled.img`
  width: 100%;
  height: auto;
  display: block;
`;

export const MockEventIcon = styled.img`
  position: absolute;
  top: 20%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 10%;
  cursor: pointer;
  transition: transform 0.2s ease;

  &:hover {
    transform: translate(-50%, -50%) scale(1.1);
  }

  &:active {
    transform: translate(-50%, -50%) scale(0.95);
  }
`;