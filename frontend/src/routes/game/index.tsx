import { useState } from 'react';
import { createFileRoute } from '@tanstack/react-router'
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


export const Route = createFileRoute('/game/')({
  component: RouteComponent,
})

function RouteComponent() {
  const [guestOpen, setGuestOpen] = useState(false);
  const [choiceOpen, setChoiceOpen] = useState(false);
  const [feedbackOpen, setFeedbackOpen] = useState(false);

  return (
    <MainContainer>
      <GameFont />
      
      <GameHeader>
        <InfoBox>
          <InfoText>광주5반 국가</InfoText>
          <InfoText>방준엽 플레이어</InfoText>
        </InfoBox>
        <TurnBox>
          <TurnText>15턴</TurnText>
        </TurnBox>
      </GameHeader>

      <ParameterBox>
        <Parameter type="eco" value={70} />
        <Parameter type="env" value={40} />
        <Parameter type="cit" value={10} />
        <Parameter type="def" value={90} />
      </ParameterBox>

      <GameMessage onClick={() => setGuestOpen(true)}>
        <Message text="새로운 손님이 도착했습니다!" />
      </GameMessage>
      {guestOpen && (
        <GuestDialog
          guestName="싸피교육생 문영호"
          guestText="밥줘."
          open
          onClose={() => setGuestOpen(false)}
          onSelect={() => {
            setGuestOpen(false);
            setChoiceOpen(true);
          }}
        />
      )}

      {choiceOpen && (
        <ChoiceDialog
          guestText="밥줘."
          open
          onBack={() => {
            setChoiceOpen(false);
            setGuestOpen(true);
          }}
          onSelect={() => {
            setChoiceOpen(false);
            setGuestOpen(false);
            setFeedbackOpen(true);
          }}
        />
      )}

      {feedbackOpen && (
        <FeedbackDialog
          open
          onClose={() => setFeedbackOpen(false)}
        />
      )}
    </MainContainer>
  )
}
