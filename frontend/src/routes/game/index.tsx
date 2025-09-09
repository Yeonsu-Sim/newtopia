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

export const Route = createFileRoute('/game/')({
  component: RouteComponent,
})

function RouteComponent() {
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

      <GameMessage>
        <Message text="새로운 손님이 도착했습니다!" />
      </GameMessage>
    </MainContainer>
  )
}
