import { useState, useEffect } from 'react'
import { useAudio } from '@/hooks/useAudio'
import { gameService, type HintData } from '@/services/game/gameService'

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
  gameId: number
  guestText: string
  choices: ChoiceOption[]
  currentStats: CountryStats
  open: boolean
  onBack: () => void
  onSelect: (choiceCode: string) => void
  initialMousePos?: { x: number; y: number } | null
}

const ChoiceDialog: React.FC<ChoiceDialogProps> = ({
  gameId,
  guestText,
  choices,
  currentStats,
  onBack,
  onSelect,
  initialMousePos = { x: 0, y: 0 },
}) => {
  const { playClickSound } = useAudio({ enableBgm: false })
  const [mousePos, setMousePos] = useState(initialMousePos || { x: 0, y: 0 })
  const [hints, setHints] = useState<HintData | null>(null)
  const [hoveredChoice, setHoveredChoice] = useState<'A' | 'B' | null>(null)

  const handleBack = () => {
    playClickSound()
    onBack()
  }

  const handleSelect = (choiceCode: string) => {
    playClickSound()
    onSelect(choiceCode)
  }

  // 힌트 데이터 로드
  useEffect(() => {
    const loadHints = async () => {
      try {
        const hintData = await gameService.fetchChoiceHints(gameId)
        setHints(hintData)
      } catch (error) {
        console.error('Failed to load choice hints:', error)
      }
    }

    if (gameId) {
      loadHints()
    }
  }, [gameId])

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
        <ParameterChange
          type="eco"
          value={currentStats.eco}
          highlightLevel={hoveredChoice && hints ? hints[hoveredChoice].economy : undefined}
        />
        <ParameterChange
          type="env"
          value={currentStats.env}
          highlightLevel={hoveredChoice && hints ? hints[hoveredChoice].environment : undefined}
        />
        <ParameterChange
          type="opi"
          value={currentStats.opi}
          highlightLevel={hoveredChoice && hints ? hints[hoveredChoice].publicSentiment : undefined}
        />
        <ParameterChange
          type="mil"
          value={currentStats.mil}
          highlightLevel={hoveredChoice && hints ? hints[hoveredChoice].defense : undefined}
        />
      </ParameterChangeBox>

      <DialogBox>
        <DialogText>{guestText}</DialogText>
        <ChoiceCards>
          {choices[0] && (
            <ChoiceCardA
              onClick={() => handleSelect(choices[0].code)}
              onMouseEnter={() => setHoveredChoice('A')}
              onMouseLeave={() => setHoveredChoice(null)}
            >
              {choices[0].label}
            </ChoiceCardA>
          )}
          {choices[1] && (
            <ChoiceCardB
              onClick={() => handleSelect(choices[1].code)}
              onMouseEnter={() => setHoveredChoice('B')}
              onMouseLeave={() => setHoveredChoice(null)}
            >
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
