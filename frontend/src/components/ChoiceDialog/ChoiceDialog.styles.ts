import styled from "styled-components";

export const DialogOverlay = styled.div`
    position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.6); 
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 20;
`;

export const DialogBox = styled.div`
  padding: 2rem;
  width: 600px;
  border-radius: 12px;
  text-align: center;
`;

export const ChoiceCards = styled.div`
  display: flex;
  gap: 1rem;
  margin: 1rem 0;
`;

export const CardA = styled.div`
  flex: 1;  
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  height:300px;
  padding: 1rem;
  background: #0883bd;
  border-radius: 8px;
  cursor: pointer;
  &:hover {
    background: #ddd;
  }
`;

export const CardB = styled.div`
  flex: 1;  
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  height:300px;
  padding: 1rem;
  background: #e49000;
  border-radius: 8px;
  cursor: pointer;
  &:hover {
    background: #ddd;
  }
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