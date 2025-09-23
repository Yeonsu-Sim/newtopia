import { createFileRoute, useNavigate } from '@tanstack/react-router'
import 'react-circular-progressbar/dist/styles.css'
import {
  Container,
  Header,
  MainContainer,
  TopRightButton,
} from '@/routes/report/-Report.styles'

import FinalScore from '@/components/FinalScore/FinalScore'
import PlayStatistics from '@/components/PlayStatistics/PlayStatistics'
import AiReport from '@/components/AiReport/AiReport'

import { useGameStore } from '@/store/gameStore'
import { useReportContext, useReportGraph } from '@/hooks/useReport'

export const Route = createFileRoute('/report/')({
  component: RouteComponent,
})

function RouteComponent() {
  const navigate = useNavigate()
  const { gameId } = useGameStore()
  const { data: context, loading: contextLoading } = useReportContext(gameId)
  const { data: graph, loading: graphLoading } = useReportGraph(gameId, 200)

  if (contextLoading || graphLoading) return <p>로딩중...</p>

  return (
    <Container>
      <MainContainer>
        <Header>국정운영 리포트</Header>

        {context && <FinalScore stats={context.data.context.countryStats} />}

        <AiReport gameId={gameId}></AiReport>

        {graph && context && (
          <PlayStatistics
            series={graph.data.graph.series}
            finalTurnNumber={context.data.context.finalTurnNumber}
            gameId={gameId}
          />
        )}
        <TopRightButton onClick={() => navigate({ to: '/' })}>
          메인으로
        </TopRightButton>
      </MainContainer>
    </Container>
  )
}
