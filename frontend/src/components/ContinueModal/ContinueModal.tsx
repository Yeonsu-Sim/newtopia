import React from 'react'
import {
  Overlay,
  ModalBox,
  ButtonGroup,
} from '@/components/ContinueModal/ContinueModal.styles'

interface ContinueModalProps {
  countryName: string
  onContinue: () => void
  onNewGame: () => void
}

export const ContinueModal: React.FC<ContinueModalProps> = ({
  countryName,
  onContinue,
  onNewGame,
}) => {
  return (
    <Overlay>
      <ModalBox>
        <h2>진행 중인 게임이 있습니다</h2>
        <p>[{countryName}] 이어서 플레이하시겠습니까?</p>
        <ButtonGroup>
          <button onClick={onContinue}>예</button>
          <button onClick={onNewGame}>아니오</button>
        </ButtonGroup>
      </ModalBox>
    </Overlay>
  )
}
