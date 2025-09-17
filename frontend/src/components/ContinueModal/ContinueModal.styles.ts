import styled from "styled-components";

export const Overlay = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const ModalBox = styled.div`
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  width: 360px;
  text-align: center;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`;

export const ButtonGroup = styled.div`
  margin-top: 16px;
  display: flex;
  justify-content: space-around;

  button {
    padding: 8px 16px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
  }
`;