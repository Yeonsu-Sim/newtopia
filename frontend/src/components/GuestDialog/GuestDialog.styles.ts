import styled from "styled-components";

export const DialogOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.6);
  display: flex;
  padding: 2rem;
  align-items: center;
  flex-direction: column;
  justify-content: flex-end;
  z-index: 1000;
`;

export const DialogBox = styled.div`
  display: flex;
  flex-direction: column;
  background: #2e2e3a;
  color: #fff;
  padding: 1.5rem 2rem;
  border-radius: 12px 12px 12px 12px;
  width: 100%;
  min-width: 40rem;
  box-shadow: 0 -4px 8px rgba(0,0,0,0.6);
  font-family: 'Cafe24ProUp', sans-serif;
`;

export const DialogHeader = styled.h2`
  margin: 0 0 0.5rem;
  font-size: 1.4rem;
`;

export const DialogContent = styled.p`
  margin: 0 0 1rem;
  font-size: 1.1rem;
`;

export const DialogActionsBox = styled.div`
    display: flex;
    justify-content: flex-end;
    gap: 10px;
`;

export const DialogActions = styled.div`
  button {
    background: #444;
    border: none;
    color: #fff;
    padding: 0.5rem 1rem;
    border-radius: 6px;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: #666;
    }
  }
`;

export const GuestIcon = styled.img.attrs({
    src: "src/assets/icons/사람1.png",
    alt: "",
})`
    width: 150px;
    height: auto;
`;

export const GuestBox = styled.div`
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: flex-end;
`;