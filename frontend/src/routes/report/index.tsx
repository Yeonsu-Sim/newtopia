import { useEffect } from 'react'
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
import { LoadingScreen } from '@/components/common/LoadingScreen'

import { useGameStore } from '@/store/gameStore'
import { useReportContext, useReportGraph } from '@/hooks/useReport'

export const Route = createFileRoute('/report/')({
  component: RouteComponent,
})

function RouteComponent() {
  const navigate = useNavigate()
  const { gameId } = useGameStore()

  // gameId 없으면 바로 메인으로
  useEffect(() => {
    if (!gameId) {
      navigate({ to: '/' })
    }
  }, [gameId, navigate])
  
  const { data: context, loading: contextLoading } = useReportContext(gameId)
  const { data: graph, loading: graphLoading } = useReportGraph(gameId, 200)

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (
        e.key === 'F5' ||
        (e.ctrlKey && e.key.toLowerCase() === 'r') ||
        (e.metaKey && e.key.toLowerCase() === 'r')
      ) {
        e.preventDefault()
        navigate({ to: '/' })
      }
    }

    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      e.preventDefault()
      window.location.href = '/' 
    }

    window.addEventListener('keydown', handleKeyDown)
    window.addEventListener('beforeunload', handleBeforeUnload)

    return () => {
      window.removeEventListener('keydown', handleKeyDown)
      window.removeEventListener('beforeunload', handleBeforeUnload)
    }
  }, [navigate])


  if (contextLoading || graphLoading) return <LoadingScreen />

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
