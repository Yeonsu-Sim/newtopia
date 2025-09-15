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
  padding: 2rem;
  width: 700px;
  height: 80%;
  border-radius: 12px;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
`;

export const DialogTitle = styled.div`
  font-size: 1.8rem;
`;

export const NewsTitle = styled.div`
  font-size: 1.2rem;
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
  align-self: flex-end;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: none;
  background: #3498db;
  color: #fff;
  cursor: pointer;
  font-weight: bold;
  margin-top: 1rem;
  &:hover {
    background: #2980b9;
  }
`;