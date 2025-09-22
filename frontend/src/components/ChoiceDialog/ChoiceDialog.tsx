import { useState, useEffect } from 'react'
import { useAudio } from '@/hooks/useAudio'

import { ParameterChangeBox } from '@/routes/game/-Game.styles'

import {
  DialogOverlay,
  DialogBox,
  DialogText,
  ChoiceCards,
  CloseButton,
  ChoiceCardA,
  ChoiceCardB,
  CustomCursorImg,
} from '@/components/ChoiceDialog/ChoiceDialog.styles'

import ParameterChange from '@/components/ParameterChange'

interface ChoiceOption {
  code: string
  label: string
}

interface CountryStats {
  eco: number
  mil: number
  opi: number
  env: number
}

interface ChoiceDialogProps {
  guestText: string
  choices: ChoiceOption[]
  currentStats: CountryStats
  open: boolean
  onBack: () => void
  onSelect: (choiceCode: string) => void
  initialMousePos?: { x: number; y: number } | null
}

const ChoiceDialog: React.FC<ChoiceDialogProps> = ({
  guestText,
  choices,
  currentStats,
  onBack,
  onSelect,
  initialMousePos = { x: 0, y: 0 },
}) => {
  const { playClickSound } = useAudio({ enableBgm: false })
  const [mousePos, setMousePos] = useState(initialMousePos || { x: 0, y: 0 })

  const handleBack = () => {
    playClickSound()
    onBack()
  }

  const handleSelect = (choiceCode: string) => {
    playClickSound()
    onSelect(choiceCode)
  }

  useEffect(() => {
    if (initialMousePos) {
      setMousePos(initialMousePos)
    }
  }, [initialMousePos])

  useEffect(() => {
    const move = (e: MouseEvent) => {
      setMousePos({ x: e.clientX, y: e.clientY })
    }
    window.addEventListener('mousemove', move)
    return () => window.removeEventListener('mousemove', move)
  }, [])

  return (
    <DialogOverlay>
      <CustomCursorImg
        src="/icons/손.png"
        style={{ left: mousePos.x + 120, top: mousePos.y + 170 }}
      />

      <ParameterChangeBox>
        <ParameterChange type="eco" value={currentStats.eco} />
        <ParameterChange type="env" value={currentStats.env} />
        <ParameterChange type="opi" value={currentStats.opi} />
        <ParameterChange type="mil" value={currentStats.mil} />
      </ParameterChangeBox>

      <DialogBox>
        <DialogText>{guestText}</DialogText>
        <ChoiceCards>
          {choices[0] && (
            <ChoiceCardA onClick={() => handleSelect(choices[0].code)}>
              {choices[0].label}
            </ChoiceCardA>
          )}
          {choices[1] && (
            <ChoiceCardB onClick={() => handleSelect(choices[1].code)}>
              {choices[1].label}
            </ChoiceCardB>
          )}
        </ChoiceCards>
        <CloseButton onClick={handleBack}>뒤로가기</CloseButton>
      </DialogBox>
    </DialogOverlay>
  )
}

export default ChoiceDialog
