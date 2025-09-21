import styled from 'styled-components'

export const LoadingContainer = styled.div`
  position: relative;
  width: 100%;
  height: 100vh;
  min-height: 100vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
`

export const LoadingContent = styled.div`
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2rem;
  text-align: center;
`

export const LoadingTitle = styled.h1`
  color: white;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  font-size: clamp(24px, 6vw, 48px);
  font-weight: normal;
  text-shadow: #000000 4px 4px 10px;
  margin: 0;
  line-height: 1.2;
  white-space: nowrap;
`

export const LottieContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
`

export const FallbackBackground = styled.div`
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
`
