import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { useGameStore } from '@/store/gameStore'
import { useAuthStore } from '@/store/authStore'
import { useAudio } from '@/hooks/useAudio'
import { useGame } from '@/hooks/useGame'
import { GameBackground } from '@/components/common/GameBackground'
import { ContinueModal } from '@/components/ContinueModal/ContinueModal'
import {
  SetupContainer,
  TitleSection,
  TitleText,
  FormSection,
  CountryNameForm,
  InputWrapper,
  InputInner,
  CountryNameInput,
  InstructionText,
  BackButton,
  IntroTextContainer,
  IntroTextLine,
} from '@/routes/game/setup/-GameSetup.styles'

export const Route = createFileRoute('/game/setup/')({
  component: GameSetupPage,
})

function GameSetupPage() {
  const navigate = useNavigate()
  const { setGameStart } = useGameStore()
  const { user } = useAuthStore()
  const { playClickSound } = useAudio()
  const { fetchOngoingGame, fetchGameById, createNewGame } = useGame()

  const [countryName, setCountryName] = useState('')
  const [showNameInput, setShowNameInput] = useState(false)
  const [ongoingGame, setOngoingGame] = useState<any>(null)
  const [showIntroAnimation, setShowIntroAnimation] = useState(false)
  const [currentTextIndex, setCurrentTextIndex] = useState(0)
  const [animationComplete, setAnimationComplete] = useState(false)
  const [gameData, setGameData] = useState<any>(null)
  const [isApiLoading, setIsApiLoading] = useState(false)

  useEffect(() => {
    const checkGame = async () => {
      try {
        const data = await fetchOngoingGame()
        if (data?.data?.game) {
          setOngoingGame(data.data.game)
        } else {
          setShowNameInput(true)
        }
      } catch (err) {
        console.error(err)
        setShowNameInput(true)
      }
    }
    checkGame()
  }, [])

  const introTexts = [
    '당신은 이 세계의 지도자가 되었습니다...',
    '이제는 국가를 운영하며',
    '국가운영지표를 균형 있게 유지하면서',
    '임기를 마쳐야합니다...',
  ]

  useEffect(() => {
    if (showIntroAnimation && currentTextIndex < introTexts.length) {
      const timer = setTimeout(() => {
        setCurrentTextIndex(currentTextIndex + 1)
      }, 1500)
      return () => clearTimeout(timer)
    } else if (showIntroAnimation && currentTextIndex >= introTexts.length) {
      setAnimationComplete(true)
    }
  }, [showIntroAnimation, currentTextIndex, introTexts.length])

  // 애니메이션과 API 둘 다 완료되면 자동으로 게임 시작
  useEffect(() => {
    if (animationComplete && gameData && !isApiLoading) {
      startGame(gameData)
    }
  }, [animationComplete, gameData, isApiLoading])

  const startGame = (game: any, isFirst: boolean = true) => {
    const turn = game.turn
    setGameStart(
      game.gameId,
      {
        eco: turn.countryStats.eco,
        mil: turn.countryStats.mil,
        opi: turn.countryStats.opi,
        env: turn.countryStats.env,
      },
      game.countryName,
      user?.nickname || '플레이어',
      turn.number,
    )

    // 새 게임인 경우 localStorage에 플래그 설정
    if (isFirst) {
      localStorage.setItem('showGameOnboarding', 'true')
    }

    navigate({ to: '/game' })
  }

  const handleContinue = async () => {
    playClickSound()
    if (!ongoingGame) return
    const data = await fetchGameById(ongoingGame.gameId)
    if (data?.data?.game) {
      startGame(data.data.game, false) // 기존 게임은 온보딩 없음
    }
  }

  const handleNewGame = () => {
    playClickSound()
    setOngoingGame(null)
    setShowNameInput(true)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    playClickSound()

    if (!countryName.trim()) {
      alert('나라 이름을 입력해주세요.')
      return
    }

    // 인트로 애니메이션과 API 호출을 동시에 시작
    setShowNameInput(false)
    setShowIntroAnimation(true)
    setCurrentTextIndex(0)
    setAnimationComplete(false)
    setIsApiLoading(true)

    // 백그라운드에서 API 호출 시작
    try {
      const data = await createNewGame(countryName.trim())
      if (data?.data?.game) {
        setGameData(data.data.game)
      }
    } catch (err) {
      console.error(err)
    } finally {
      setIsApiLoading(false)
    }
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCountryName(e.target.value)
  }

  const handleBack = () => {
    playClickSound()
    navigate({ to: '/main' })
  }

  const handleSkipAnimation = async () => {
    if (showIntroAnimation && !animationComplete) {
      setCurrentTextIndex(introTexts.length)
      setAnimationComplete(true)

      // API가 아직 완료되지 않았다면 완료될 때까지 기다림
      if (isApiLoading || !gameData) {
        // API 완료를 기다린 후 즉시 게임 시작
        if (isApiLoading) {
          // API 완료를 기다리는 동안 로딩 상태 유지
          return
        }
      }

      // API가 이미 완료되었다면 즉시 게임 시작
      if (gameData && !isApiLoading) {
        startGame(gameData)
      }
    }
  }

  return (
    <SetupContainer>
      <GameBackground variant="setup" />
      <BackButton onClick={handleBack}>← 뒤로가기</BackButton>

      {ongoingGame && !showNameInput && (
        <ContinueModal
          countryName={ongoingGame.countryName}
          onContinue={handleContinue}
          onNewGame={handleNewGame}
        />
      )}

      {showNameInput && (
        <>
          <TitleSection>
            <TitleText>
              <p>통치할 나라의 이름을 입력해주세요</p>
            </TitleText>
          </TitleSection>

          <FormSection>
            <CountryNameForm onSubmit={handleSubmit}>
              <InputWrapper>
                <InputInner>
                  <CountryNameInput
                    type="text"
                    value={countryName}
                    onChange={handleInputChange}
                    placeholder="이름"
                    maxLength={20}
                    required
                  />
                </InputInner>
              </InputWrapper>
              <button type="submit" style={{ display: 'none' }}>
                제출
              </button>
            </CountryNameForm>
          </FormSection>

          <InstructionText>
            <p>Enter를 눌러 계속하기</p>
          </InstructionText>
        </>
      )}

      {showIntroAnimation && (
        <IntroTextContainer onClick={handleSkipAnimation}>
          <div>
            {introTexts.slice(0, currentTextIndex + 1).map((text, index) => (
              <IntroTextLine
                key={index}
                $isActive={index === currentTextIndex}
                style={{
                  opacity: index <= currentTextIndex ? 1 : 0,
                  transform:
                    index <= currentTextIndex
                      ? 'translateY(0)'
                      : 'translateY(20px)',
                  transition: 'all 1s ease',
                  transitionDelay: '0.2s',
                }}
              >
                {text}
              </IntroTextLine>
            ))}
          </div>

          {animationComplete && (isApiLoading || !gameData) && (
            <InstructionText>
              <p>시나리오 준비 중...</p>
            </InstructionText>
          )}

          {!animationComplete && (
            <InstructionText>
              <p>클릭하여 건너뛰기</p>
            </InstructionText>
          )}
        </IntroTextContainer>
      )}
    </SetupContainer>
  )
}
