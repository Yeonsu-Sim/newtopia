import { useState, useEffect } from 'react';
import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useGameStore } from "@/store/gameStore";
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
} from '@/routes/game/-Game.styles';

import Parameter from "@/components/Parameter";
import GuestDialog from '@/components/GuestDialog/GuestDialog';
import ChoiceDialog from '@/components/ChoiceDialog/ChoiceDialog';
import FeedbackDialog from '@/components/FeedbackDialog/FeedbackDialog';

import { useGame } from '@/hooks/useGame';
import { useGamePlay } from '@/hooks/useGamePlay';
import { useAuthStore } from '@/store/authStore';
import FeedbackToastContainer from '@/components/FeedbackToast/FeedbackToastContainer';
import { HotTopic } from '@/components/common/HotTopic/HotTopic';

export const Route = createFileRoute('/game/')({
  component: RouteComponent,
});

function RouteComponent() {
  const [guestOpen, setGuestOpen] = useState(false);
  const [choiceOpen, setChoiceOpen] = useState(false);
  const [feedbackOpen, setFeedbackOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const { currentStats, currentTurn, countryName, playerName, setGameStart, setStats, setTurn } = useGameStore();
  const [currentArticle, setCurrentArticle] = useState<any>(null);
  const [currentCard, setCurrentCard] = useState<any>(null);
  const [gameId, setGameId] = useState<number | null>(null);

  const { fetchOngoingGame, fetchGameById, createNewGame } = useGame();
  const { submitChoice } = useGamePlay();
  const { user } = useAuthStore();
  const navigate = useNavigate();

  const [clickPos, setClickPos] = useState<{ x: number; y: number } | null>(null);

  const [toastMessages, setToastMessages] = useState<string[]>([]);

  const dummyComments = [
    "이럴 때 부양 가자! 내 노후자금 회복 좀!",
    "외국인 들어올 때 규제 푸는 건 위험. 빠질 땐 누가 책임?",
    "반도체 세액공제 확대 찬성, 일자리 늘어난다."
  ];

  useEffect(() => {
    if (!user) return;
    const initGame = async () => {
      try {
        const ongoing = await fetchOngoingGame();

        if (ongoing?.data?.game) {
          const gameData = await fetchGameById(ongoing.data.game.gameId);
          const startTurn = gameData.data.game.turn;

          setGameId(gameData.data.game.gameId);
          setGameStart(
            gameData.data.game.gameId,
            startTurn.countryStats,
            gameData.data.game.countryName,
            user?.nickname || "플레이어",
            startTurn.number
          );

          setCurrentCard(startTurn.card);
          setCurrentArticle(startTurn.card.relatedArticle);
        } else {
          const newGame = await createNewGame(countryName.trim());
          const startTurn = newGame.data.game.turn;

          setGameId(newGame.data.game.gameId);
          setGameStart(
            newGame.data.game.gameId,
            startTurn.countryStats,
            newGame.data.game.countryName,
            user?.nickname || "플레이어",
            startTurn.number
          );

          setCurrentCard(startTurn.card);
          setCurrentArticle(startTurn.card.relatedArticle);
        }
      } catch (err) {
        console.error(err);
      }
    };

    initGame();
  }, [user]);

  const handleChoice = async (choiceCode: "A" | "B") => {
    if (!gameId || !currentCard) return;

    setCurrentArticle(currentCard.relatedArticle);
    setGuestOpen(false);
    setChoiceOpen(false);
    setFeedbackOpen(true);

    try {
      setLoading(true);
      const result = await submitChoice(gameId, currentCard.cardId, choiceCode);

      if (result.gameOver && result.ending?.code) {
        navigate({
            to: `/ending?endingCode=${result.ending.code}`,
        });
        return;
      }

      const nextTurn = result.nextTurn;
      if (nextTurn) {
        setCurrentCard(nextTurn.card);
        setStats(nextTurn.countryStats);
        setTurn(nextTurn.number);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChoiceWrapper = (choiceCode: string) => {
    handleChoice(choiceCode as "A" | "B");
  };

  const handleFeedbackClose = () => {
    setFeedbackOpen(false);
    const messages = currentCard?.comments || dummyComments;
    setToastMessages(messages);
  };

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
            <EventIcon src="/icons/말풍선.png" x={50} y={20} onClick={() => setGuestOpen(true)} />
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
            setClickPos({ x: e.clientX, y: e.clientY });
            setGuestOpen(false);
            setChoiceOpen(true);
          }}
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
            setChoiceOpen(false);
            setGuestOpen(true);
          }}
          onSelect={handleChoiceWrapper}
        />
      )}

      {feedbackOpen && (
        <FeedbackDialog
          open
          article={currentArticle}
          onClose={handleFeedbackClose}
        />
      )}
      
    {toastMessages.length > 0 && (
      <FeedbackToastContainer
        messages={toastMessages}
      />
    )}

    <HotTopic />
    </MainContainer>
  );
}