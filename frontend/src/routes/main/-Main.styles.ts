import styled from 'styled-components'

export const MainContainer = styled.div`
  position: relative;
  width: 100%;
  height: 100vh;
  min-height: 100vh;
  overflow: hidden;
`

export const WelcomeSection = styled.div`
  position: absolute;
  top: 10%;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  z-index: 10;
`

export const WelcomeTitle = styled.h1`
  color: white;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: clamp(32px, 8vw, 64px);
  font-weight: normal;
  text-shadow: #000000 4px 4px 10px;
  margin-bottom: 1rem;
  line-height: 1.2;
  white-space: nowrap;
`

export const WelcomeSubtitle = styled.p`
  color: #fef3c7;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: clamp(16px, 4vw, 24px);
  font-weight: 500;
  text-shadow: #000000 2px 2px 4px;
  line-height: 1.4;
`

export const MenuContainer = styled.div`
  position: absolute;
  left: 50%;
  top: 60%;
  transform: translate(-50%, -50%);
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0 1rem;
`

export const BgmToggleButton = styled.button`
  position: absolute;
  top: 2rem;
  left: 2rem;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 24px;
  cursor: pointer;
  z-index: 20;
  backdrop-filter: blur(4px);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: rgba(0, 0, 0, 0.8);
    transform: scale(1.1);
  }

  &:active {
    transform: scale(0.95);
  }
`

export const LogoutButton = styled.button`
  position: absolute;
  top: 2rem;
  right: 2rem;
  background: #dc2626;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 20;

  &:hover {
    background: #b91c1c;
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`
export const NoticeIcon = styled.img`
  position: absolute;
  bottom: 10%;
  left: 3%;  
  width: 70px;
  height: auto;
  cursor: pointer;
  display: block;
  z-index: 100;
`;