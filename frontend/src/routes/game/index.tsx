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
  ParameterBox,
  GameMessage
} from '@/routes/game/-Game.styles';

import Parameter from "@/components/Parameter";
import Message from "@/components/Message";
import GuestDialog from '@/components/GuestDialog/GuestDialog';
import ChoiceDialog from '@/components/ChoiceDialog/ChoiceDIalog';
import FeedbackDialog from '@/components/FeedbackDialog/FeedbackDialog';

import { useGame } from '@/hooks/useGame';
import { useGamePlay } from '@/hooks/useGamePlay';
import { useAuthStore } from '@/store/authStore';

export const Route = createFileRoute('/game/')({
  component: RouteComponent,
});

function RouteComponent() {
  const [guestOpen, setGuestOpen] = useState(false);
  const [choiceOpen, setChoiceOpen] = useState(false);
  const [feedbackOpen, setFeedbackOpen] = useState(false);

  const { currentStats, currentTurn, countryName, playerName, setGameStart, setStats, setTurn } = useGameStore();
  const [currentArticle, setCurrentArticle] = useState<any>(null);
  const [currentCard, setCurrentCard] = useState<any>(null);
  const [gameId, setGameId] = useState<number | null>(null);

  const { fetchOngoingGame, fetchGameById, createNewGame } = useGame();
  const { submitChoice } = useGamePlay();
  const { user } = useAuthStore();
  const navigate = useNavigate();

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

    try {
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
        setCurrentArticle(nextTurn.card.relatedArticle);
        setStats(nextTurn.countryStats);
        setTurn(nextTurn.number);

        setGuestOpen(false);
        setChoiceOpen(false);
        setFeedbackOpen(true);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleChoiceWrapper = (choiceCode: string) => {
    handleChoice(choiceCode as "A" | "B");
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

      <ParameterBox>
        {currentStats && (
          <>
            <Parameter type="eco" value={currentStats.eco} />
            <Parameter type="env" value={currentStats.env} />
            <Parameter type="opi" value={currentStats.opi} />
            <Parameter type="mil" value={currentStats.mil} />
          </>
        )}
      </ParameterBox>

      <GameMessage onClick={() => setGuestOpen(true)}>
        <Message text="새로운 손님이 도착했습니다!" />
      </GameMessage>

      {guestOpen && currentCard && (
        <GuestDialog
          guestName={currentCard.npc.name}
          guestText={currentCard.content}
          open
          onClose={() => setGuestOpen(false)}
          onSelect={() => {
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
          onBack={() => {
            setChoiceOpen(false);
            setGuestOpen(true);
          }}
          onSelect={handleChoiceWrapper} // 타입 안전하게
        />
      )}

      {feedbackOpen && (
        <FeedbackDialog
          open
          article={currentArticle}
          onClose={() => setFeedbackOpen(false)}
        />
      )}
    </MainContainer>
  );
}
