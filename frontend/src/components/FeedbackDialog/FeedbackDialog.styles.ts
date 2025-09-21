import styled from "styled-components";

export const DialogOverlay = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
`;

export const DialogBox = styled.div`
  background: #2e2e3a;
  padding: 1rem;
  width: 700px;
  height: 90%;
  border-radius: 12px;
  text-align: center;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
`;

export const DialogTitle = styled.div`
  font-size: 1.8rem;
`;

export const NewsTitle = styled.div`
  font-size: 1.6rem;
  flex-shrink: 0;
  margin-bottom: 1rem;
  
  a {
    color: inherit;
    text-decoration: underline;
    transition: all 0.2s ease;
    
    &:hover {
      color: #3498db;
      text-decoration: none;
    }
  }
`;

export const CommentBox = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
`;

export const Comment = styled.div`
  background: #fff;
  color: #2e2e3a;
  padding: 2rem;
  width: 200px;
  border-radius: 12px;
`;

export const CloseButton = styled.button`
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  background: rgba(52, 152, 219, 0.9);
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  color: white;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: bold;
  z-index: 10;
  
  &:hover {
    background: rgba(41, 128, 185, 0.9);
  }
`;

export const ContentContainer = styled.div`
  margin-top: 0.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
`;

export const ContentArea = styled.div`
  flex: 1;
  text-align: left;
  font-size: 0.9rem;
  line-height: 1.6;
  overflow-y: auto;
  padding: 1.2rem;
  background-color: #3a3a47;
  border-radius: 8px;
  border: 1px solid #4a4a57;
  scrollbar-width: thin;
  min-height: 0;
`;

export const ContentText = styled.pre`
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  font-family: Gmarket Sans TTF;
  font-size: 1rem;
  line-height: 1.2; 
`;