import { useState, useEffect } from 'react'
import { createFileRoute, useNavigate, useSearch } from '@tanstack/react-router'
import { useGameStore } from '@/store/gameStore'
import {
  MainContainer,
  GameFont,
  GameHeader,
  TurnBox,
  InfoBox,
  InfoText,
  TurnText,
  BackgroundWrapper,
  BackgroundImage,
  EventIcon,
  LodingIcon,
} from '@/routes/game/-Game.styles'

import Parameter from '@/components/Parameter'
import GuestDialog from '@/components/GuestDialog/GuestDialog'
import ChoiceDialog from '@/components/ChoiceDialog/ChoiceDialog.tsx'
import FeedbackDialog from '@/components/FeedbackDialog/FeedbackDialog'
import ArticleDialog from '@/components/ArticleDialog/ArticleDialog'

import { useGame } from '@/hooks/useGame'
import { useGamePlay } from '@/hooks/useGamePlay'
import { useAuthStore } from '@/store/authStore'
import { useAudio } from '@/hooks/useAudio'
// import FeedbackToastContainer from '@/components/FeedbackToast/FeedbackToastContainer'
import { HotTopic } from '@/components/common/HotTopic/HotTopic'

export const Route = createFileRoute('/game/')({
  component: RouteComponent,
  validateSearch: (search: Record<string, unknown>) => {
    return {
      isFirstGame: search?.isFirstGame === 'true',
    }
  },
})

function RouteComponent() {
  const [guestOpen, setGuestOpen] = useState(false)
  const [choiceOpen, setChoiceOpen] = useState(false)
  const [articleOpen, setArticleOpen] = useState(false)
  const [feedbackOpen, setFeedbackOpen] = useState(false)
  const [loading, setLoading] = useState(false)

  const {
    currentStats,
    currentTurn,
    countryName,
    playerName,
    setGameStart,
    setStats,
    setTurn,
  } = useGameStore()
  const [currentArticle, setCurrentArticle] = useState<any>(null)
  const [currentCard, setCurrentCard] = useState<any>(null)
  const [gameId, setGameId] = useState<number | null>(null)
  const [selectedChoiceCode, setSelectedChoiceCode] = useState<
    'A' | 'B' | null
  >(null)
  const [showEventIcon, setShowEventIcon] = useState(false)
  const [eventIconAnimation, setEventIconAnimation] = useState(false)
  const [onboardingOpen, setOnboardingOpen] = useState(false)
  const [, setIsFirstGame] = useState(false)
  const [onboardingStep, setOnboardingStep] = useState(0)
  const [pendingTurnData, setPendingTurnData] = useState<any>(null)

  const { fetchOngoingGame, fetchGameById, createNewGame } = useGame()
  const { submitChoice } = useGamePlay()
  const { user } = useAuthStore()
  const navigate = useNavigate()
  const search = useSearch({ from: '/game/' })
  useAudio({ enableBgm: false })

  const [clickPos, setClickPos] = useState<{ x: number; y: number } | null>(
    null,
  )

  // const [toastMessages, setToastMessages] = useState<string[]>([])

  // 온보딩 데이터
  const onboardingTexts = [
    '좋은 아침입니다 시장님. 오늘부터 시장님을 보좌하게될 보좌관 뉴토라고 합니다.',
    '저 창문 너너로 보이는 네 개의 섬이 보이시죠?',
    '경제, 환경, 민심, 국방… 나라를 지탱하는 네 가지 기둥입니다.',
    '매 턴마다 민원인들이 찾아와 제안을 올릴 겁니다. 수용할지 거절할지는 오직 시장님의 몸입니다..',
    '다만… 그 선택 하나로 섬들의 지표가 오르내리게 됩니다.',
    '결정을 내리신 뒤에는, 시민들의 반응과 민원인의 제안에 영향을 준 실제 기사도 확인하실 수 있습니다.',
    '명심하세요! 어느 한 지표라도 0이나 100에 도달하면 시장님의 임기는 그 즉시 끝나게 됩니다.',
    '뉴스의 흐름을 예상하고, 모든 지표를 균형 있게 유지하세요.',
    '자, 이제 나만의 뉴토피아를 만들어가실 시간입니다.',
  ]

  // const dummyComments = [
  //   '이럴 때 부양 가자! 내 노후자금 회복 좀!',
  //   '외국인 들어올 때 규제 푸는 건 위험. 빠질 땐 누가 책임?',
  //   '반도체 세액공제 확대 찬성, 일자리 늘어난다.',
  // ]

  useEffect(() => {
    if (!user) return
    const initGame = async () => {
      try {
        const ongoing = await fetchOngoingGame()

        if (ongoing?.data?.game) {
          const gameData = await fetchGameById(ongoing.data.game.gameId)
          const startTurn = gameData.data.game.turn

          setGameId(gameData.data.game.gameId)
          setGameStart(
            gameData.data.game.gameId,
            startTurn.countryStats,
            gameData.data.game.countryName,
            user?.nickname || '플레이어',
            startTurn.number,
          )

          setCurrentCard(startTurn.card)
          setCurrentArticle(startTurn.card.relatedArticle)

          // 처음 게임인 경우 onboarding 표시, 그렇지 않으면 말풍선 아이콘 표시
          if (search.isFirstGame) {
            // 처음 게임인 경우 온보딩 다이얼로그 표시
            setTimeout(() => {
              setOnboardingOpen(true)
            }, 500)
          } else {
            // 말풍선 아이콘 애니메이션과 사운드
            setTimeout(() => {
              setShowEventIcon(true)
              setEventIconAnimation(true)
              // 팝업 사운드 재생
              const popSound = new Audio('/sounds/game-bonus-02-294436.mp3')
              popSound.volume = 0.7
              popSound.play().catch(console.error)

              // 애니메이션 클래스 제거
              setTimeout(() => {
                setEventIconAnimation(false)
              }, 600)
            }, 100)
          }
        } else {
          const newGame = await createNewGame(countryName.trim())
          const startTurn = newGame.data.game.turn

          setGameId(newGame.data.game.gameId)
          setGameStart(
            newGame.data.game.gameId,
            startTurn.countryStats,
            newGame.data.game.countryName,
            user?.nickname || '플레이어',
            startTurn.number,
          )

          setCurrentCard(startTurn.card)
          setCurrentArticle(startTurn.card.relatedArticle)
          setIsFirstGame(true)

          // 처음 게임인 경우 온보딩 다이얼로그 표시
          setTimeout(() => {
            setOnboardingOpen(true)
          }, 500)
        }
      } catch (err) {
        console.error(err)
      }
    }

    initGame()
  }, [user])

  const handleChoice = async (choiceCode: 'A' | 'B') => {
    if (!gameId || !currentCard) return

    setSelectedChoiceCode(choiceCode)
    setCurrentArticle(currentCard.relatedArticle)
    setGuestOpen(false)
    setChoiceOpen(false)
    setArticleOpen(true)

    try {
      setLoading(true)
      const result = await submitChoice(gameId, currentCard.cardId, choiceCode)

      if (result.gameOver && result.ending?.code) {
        navigate({
          to: `/ending?endingCode=${result.ending.code}`,
        })
        return
      }

      const nextTurn = result.nextTurn
      if (nextTurn) {
        // 다음 턴 데이터를 임시 저장만 하고, FeedbackDialog 닫힌 후 적용
        setPendingTurnData(nextTurn)
      }
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleChoiceWrapper = (choiceCode: string) => {
    handleChoice(choiceCode as 'A' | 'B')
  }

  const handleFeedbackClose = () => {
    setFeedbackOpen(false)

    // 지연된 다음 턴 데이터 적용
    if (pendingTurnData) {
      setCurrentCard(pendingTurnData.card)
      setStats(pendingTurnData.countryStats)
      setTurn(pendingTurnData.number)
      setPendingTurnData(null)

      // 새로운 턴에서 말풍선 아이콘 다시 표시
      setShowEventIcon(false)
      setTimeout(() => {
        setShowEventIcon(true)
        setEventIconAnimation(true)
        // 팝업 사운드 재생
        const popSound = new Audio('/sounds/game-bonus-02-294436.mp3')
        popSound.volume = 0.7
        popSound.play().catch(console.error)

        // 애니메이션 클래스 제거
        setTimeout(() => {
          setEventIconAnimation(false)
        }, 600)
      }, 500)
    }
  }

  // 온보딩 다이얼로그 핸들러
  const handleOnboardingClose = () => {
    setOnboardingOpen(false)
    setOnboardingStep(0)
    // 온보딩 완료 후 말풍선 아이콘 표시
    setTimeout(() => {
      setShowEventIcon(true)
      setEventIconAnimation(true)
      const popSound = new Audio('/sounds/game-bonus-02-294436.mp3')
      popSound.volume = 0.7
      popSound.play().catch(console.error)

      setTimeout(() => {
        setEventIconAnimation(false)
      }, 600)
    }, 300)
  }

  const handleOnboardingNext = () => {
    if (onboardingStep < onboardingTexts.length - 1) {
      setOnboardingStep(onboardingStep + 1)
    } else {
      // 마지막 단계에서 온보딩 완료
      setOnboardingOpen(false)
      setOnboardingStep(0)
      setTimeout(() => {
        setShowEventIcon(true)
        setEventIconAnimation(true)
        const popSound = new Audio('/sounds/game-bonus-02-294436.mp3')
        popSound.volume = 0.7
        popSound.play().catch(console.error)

        setTimeout(() => {
          setEventIconAnimation(false)
        }, 600)
      }, 300)
    }
  }

  // ArticleDialog 핸들러
  const handleViewRelatedArticle = () => {
    setArticleOpen(false)
    setFeedbackOpen(true)
  }

  return (
    <MainContainer>
      <GameFont />

      <GameHeader>
        <InfoBox>
          <InfoText>{countryName}</InfoText>
          <InfoText>{playerName} 플레이어</InfoText>
        </InfoBox>
        <TurnBox>
          <TurnText>{currentTurn ?? 0}턴</TurnText>
        </TurnBox>
      </GameHeader>

      <BackgroundWrapper>
        <BackgroundImage src="/backgrounds/game_background.png" />
        {loading ? (
          <LodingIcon src="/icons/로딩중.png" x={50} y={20} />
        ) : (
          showEventIcon && (
            <EventIcon
              src="/icons/말풍선.png"
              x={50}
              y={20}
              className={eventIconAnimation ? 'pop-animation' : ''}
              onClick={() => setGuestOpen(true)}
            />
          )
        )}
        {currentStats && (
          <>
            <Parameter type="eco" value={currentStats.eco} x={10} y={53} />
            <Parameter type="env" value={currentStats.env} x={29} y={54} />
            <Parameter type="opi" value={currentStats.opi} x={72} y={54} />
            <Parameter type="mil" value={currentStats.mil} x={90} y={54} />
          </>
        )}
      </BackgroundWrapper>

      {guestOpen && currentCard && (
        <GuestDialog
          guestName={currentCard.npc.name}
          guestText={currentCard.content}
          guestImage={currentCard.npc.imageUrl}
          open
          onClose={() => setGuestOpen(false)}
          onSelect={(e) => {
            setClickPos({ x: e.clientX, y: e.clientY })
            setGuestOpen(false)
            setChoiceOpen(true)
          }}
          enableTypewriter={true}
        />
      )}

      {choiceOpen && currentCard && (
        <ChoiceDialog
          guestText={currentCard.content}
          choices={currentCard.choices}
          currentStats={currentStats!}
          open
          initialMousePos={clickPos}
          onBack={() => {
            setChoiceOpen(false)
            setGuestOpen(true)
          }}
          onSelect={handleChoiceWrapper}
        />
      )}

      {/* ArticleDialog */}
      {articleOpen && currentCard && selectedChoiceCode && (
        <ArticleDialog
          open={articleOpen}
          playerName={playerName}
          countryName={countryName}
          currentTurn={currentTurn ?? 1}
          selectedChoice={{
            code: selectedChoiceCode,
            label:
              currentCard.choices?.find(
                (choice: any) => choice.code === selectedChoiceCode,
              )?.label || '',
            comments:
              currentCard.choices?.find(
                (choice: any) => choice.code === selectedChoiceCode,
              )?.comments || [],
          }}
          onClose={undefined}
          onViewRelatedArticle={handleViewRelatedArticle}
        />
      )}

      {feedbackOpen && (
        <FeedbackDialog
          open
          article={currentArticle}
          onClose={handleFeedbackClose}
        />
      )}

      {/* 온보딩 다이얼로그 */}
      {onboardingOpen && (
        <GuestDialog
          guestName="보좌관 뉴토"
          guestText={onboardingTexts[onboardingStep]}
          guestImage="/icons/npc1.png"
          open
          onClose={handleOnboardingClose}
          onSelect={handleOnboardingNext}
          variant="onboarding"
          closeButtonText="건너뛰기"
          selectButtonText={
            onboardingStep === onboardingTexts.length - 1 ? '시작하기' : '다음'
          }
          enableTypewriter={true}
        />
      )}

      {/* 기존 토스트 메시지 (ArticleDialog에서 댓글로 대체) */}
      {/* {toastMessages.length > 0 && (
        <FeedbackToastContainer messages={toastMessages} />
      )} */}

      <HotTopic />
    </MainContainer>
  )
}
