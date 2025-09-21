import { CircularProgressChart } from '@/components/CircularProgress/CircularProgressChart'
import {
  Section,
  SectionTitle,
  ScoreSection,
  Wrapper,
  Label,
} from '@/components/FinalScore/FinalScore.styles'

interface Props {
  stats?: {
    economy: number
    environment: number
    publicSentiment: number
    defense: number
  }
}

export default function FinalScore({ stats }: Props) {
  if (!stats)
    return (
      <Wrapper>
        <SectionTitle>최종점수</SectionTitle>
        <Section>
          <Label>점수 데이터를 불러오지 못했습니다.</Label>
        </Section>
      </Wrapper>
    )

  return (
    <Wrapper>
      <SectionTitle>최종점수</SectionTitle>
      <Section>
        <ScoreSection style={{ display: 'flex', gap: '20px' }}>
          <CircularProgressChart
            value={stats.economy}
            size="100px"
            pathColor="#5F81FF"
            trailColor="#DFE8FF"
            label="경제"
          />
          <CircularProgressChart
            value={stats.defense}
            size="100px"
            pathColor="#FF6B6B"
            trailColor="rgba(255, 219, 208, 1)"
            label="국방"
          />
          <CircularProgressChart
            value={stats.publicSentiment}
            size="100px"
            pathColor="#FFD93D"
            trailColor="#fff4c7ff"
            label="민심"
          />
          <CircularProgressChart
            value={stats.environment}
            size="100px"
            pathColor="#00C49F"
            trailColor="#c6fff5ff"
            label="환경"
          />
        </ScoreSection>
      </Section>
    </Wrapper>
  )
}
