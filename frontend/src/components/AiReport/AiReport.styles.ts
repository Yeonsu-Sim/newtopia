import styled from 'styled-components'

export const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  
  /* 모바일 최적화 */
  @media (max-width: 768px) {
    padding: 0 0.75rem;
  }

  @media (max-width: 480px) {
    padding: 0 0.5rem;
  }
`

export const Section = styled.section`
  display: flex;
  align-items: flex-start;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  overflow-wrap: break-word;
  word-wrap: break-word;
  
  /* 모바일 최적화 */
  @media (max-width: 768px) {
    padding: 1rem;
    margin-bottom: 1.25rem;
    border-radius: 8px;
  }

  @media (max-width: 480px) {
    padding: 0.75rem;
    margin-bottom: 0.75rem;
    border-radius: 6px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    margin-left: 0;
    margin-right: 0;
  }
`

export const SectionTitle = styled.h2`
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #fff;
  text-align: left;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  width: 100%;
  max-width: 100%;
  word-break: keep-all;
  overflow-wrap: break-word;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  
  /* 모바일 최적화 */
  @media (max-width: 768px) {
    font-size: 1.3rem;
    margin-bottom: 0.75rem;
  }

  @media (max-width: 480px) {
    font-size: 1.1rem;
    margin-bottom: 0.6rem;
    line-height: 1.3;
  }
`

export const Label = styled.div`
  font-size: 19px;
  color: #333;
  font-family: 'PFStardustExtraBold', 'Noto Sans KR', sans-serif;
  line-height: 1.4;
  margin-bottom: 0.5rem;
  width: 100%;
  max-width: 100%;
  word-break: keep-all;
  overflow-wrap: break-word;
  box-sizing: border-box;
  
  /* 모바일 최적화 */
  @media (max-width: 768px) {
    font-size: 17px;
    margin-bottom: 0.4rem;
  }

  @media (max-width: 480px) {
    font-size: 15px;
    margin-bottom: 0.3rem;
    line-height: 1.3;
  }
`

export const Content = styled.div`
  font-size: 16px;
  color: #333;
  font-family: 'PFStardustBold', 'Noto Sans KR', sans-serif;
  line-height: 1.5;
  margin-bottom: 0.3rem;
  word-break: keep-all;
  width: 100%;
  max-width: 100%;
  overflow-wrap: break-word;
  box-sizing: border-box;
  
  /* 모바일 최적화 */
  @media (max-width: 768px) {
    font-size: 14px;
    line-height: 1.4;
    margin-bottom: 0.25rem;
  }

  @media (max-width: 480px) {
    font-size: 13px;
    line-height: 1.4;
    margin-bottom: 0.2rem;
    word-break: break-word;
  }
  
  &:last-child {
    margin-bottom: 0;
  }
`
