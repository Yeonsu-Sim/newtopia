import { useState, useEffect } from 'react';
import { createFileRoute } from '@tanstack/react-router';
import { useGameStore } from "@/store/gameStore";
import{
  MainContainer,
  GameFont,
  GameHeader,
  TurnBox,
  InfoBox,
  InfoText,
  TurnText,
  ParameterBox,
  GameMessage
} from '@/routes/game/-Game.styles'

import Parameter from "@/components/Parameter";
import Message from "@/components/Message";
import GuestDialog from '@/components/GuestDialog/GuestDialog';
import ChoiceDialog from '@/components/ChoiceDialog/ChoiceDIalog';
import FeedbackDialog from '@/components/FeedbackDialog/FeedbackDialog';

import { exampleStartResponse, exampleChoiceResponse } from '@/data/exampleResponse';

export const Route = createFileRoute('/game/')({
  component: RouteComponent,
})

function RouteComponent() {
  const [guestOpen, setGuestOpen] = useState(false);
  const [choiceOpen, setChoiceOpen] = useState(false);
  const [feedbackOpen, setFeedbackOpen] = useState(false);

  const { currentStats, currentTurn, countryName, playerName, setGameStart, setStats, setTurn } = useGameStore();
  const [currentArticle, setCurrentArticle] = useState<any>(null);
  const [currentCard, setCurrentCard] = useState<any>(null);

  useEffect(() => {
    const startTurn = exampleStartResponse.data.game.turn;
    setGameStart(
      exampleStartResponse.data.game.gameId, 
      startTurn.countryStats, 
      exampleStartResponse.data.game.countryName, 
      "방준엽", 
      startTurn.number
    );
    setCurrentCard(startTurn.card);
    setCurrentArticle(startTurn.card.relatedArticle);
  }, []);

  const handleChoice = (_choice: string) => {
    const nextTurn = exampleChoiceResponse.data.nextTurn;

    setCurrentCard(nextTurn.card);
    setCurrentArticle(nextTurn.card.relatedArticle);
    setStats(nextTurn.countryStats);
    setTurn(nextTurn.number);

    setGuestOpen(false);
    setChoiceOpen(false);
    setFeedbackOpen(true);
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
          <TurnText>{currentTurn}턴</TurnText>
        </TurnBox>
      </GameHeader>

      <ParameterBox>
        <Parameter type="eco" value={currentStats.eco} />
        <Parameter type="env" value={currentStats.env} />
        <Parameter type="cit" value={currentStats.opi} />
        <Parameter type="def" value={currentStats.mil} />
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
          currentStats={currentStats}
          open
          onBack={() => {
            setChoiceOpen(false);
            setGuestOpen(true);
          }}
          onSelect={handleChoice}
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
  )
}