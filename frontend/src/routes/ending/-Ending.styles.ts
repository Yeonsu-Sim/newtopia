import styled from 'styled-components';

export const Container = styled.div`
  width: 100vw;
  height: 100vh;
  background-color: black;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  flex-direction: column;
  color: white;
`;

export const EndingImage = styled.img`
  max-width: 60%;
  max-height: 40%;
  object-fit: contain;
  margin-bottom: 2rem;
`;

export const EndingText = styled.div`
  text-align: center;
  font-size: 1.5rem;
`;

export const TopRightButton = styled.button`
  position: absolute;
  top: 1rem;
  right: 1rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
`;