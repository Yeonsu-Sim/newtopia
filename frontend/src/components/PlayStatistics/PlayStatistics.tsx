import { useState } from 'react'
import ReportLineChart from '@/components/ReportLineChart/ReportLineChart'
import {
  Section,
  SectionTitle,
  Wrapper,
  ChartWrapper,
  Label,
  SectionHeader,
  ButtonGroup,
  MetricButton,
  TurnNumber,
  DetailHeader,
  NpcIcon,
  NpcName,
  NpcWrapper,
  NpcTextBox,
  NpcText,
  ChoiceCards,
  ChoiceCard,
  ChoiceWrapper,
  StatsGraphWrapper,
  MetricColumn,
  Bars,
  Bar,
  GraphWrapper,
  DetailSection,
  ArticleWrapper,
  ArticleTitle,
  ArticleContent,
  ArticleUrl,
} from '@/components/PlayStatistics/PlayStatistics.styles'

import { useGameResultTurnDetail } from '@/hooks/useReport'

interface Props {
  series?: any[]
  finalTurnNumber: number
  gameId: number
}

const metricLabels: { [key: string]: string } = {
  economy: '경제',
  environment: '환경',
  publicSentiment: '민심',
  defense: '국방',
}

export default function PlayStatistics({
  series,
  finalTurnNumber,
  gameId,
}: Props) {
  const [selectedMetric, setSelectedMetric] = useState<
    'all' | 'economy' | 'environment' | 'publicSentiment' | 'defense'
  >('all')
  const [selectedTurn, setSelectedTurn] = useState<number | null>(null)

  const { data: turnDetail } = useGameResultTurnDetail(
    gameId,
    selectedTurn ?? 0,
  )

  if (!series) {
    return (
      <Wrapper>
        <SectionTitle>플레이 통계</SectionTitle>
        <Section>
          <Label>통계 데이터를 불러오지 못했습니다.</Label>
        </Section>
      </Wrapper>
    )
  }

  const filteredSeries =
    selectedMetric === 'all'
      ? series
      : series.filter((s) => s.metric === selectedMetric)

  const choiceCode = turnDetail?.data?.applied?.choiceCode

  return (
    <Wrapper>
      <SectionTitle>플레이 통계</SectionTitle>
      <SectionHeader>
        <ButtonGroup>
          <MetricButton
            $active={selectedMetric === 'all'}
            onClick={() => setSelectedMetric('all')}
          >
            전체
          </MetricButton>

          <MetricButton
            $active={selectedMetric === 'economy'}
            $activeColor="#5F81FF"
            $activeHoverColor="#3A5BCC"
            onClick={() => setSelectedMetric('economy')}
          >
            경제
          </MetricButton>

          <MetricButton
            $active={selectedMetric === 'defense'}
            $activeColor="#FF6B6B"
            $activeHoverColor="#CC4E4E"
            onClick={() => setSelectedMetric('defense')}
          >
            국방
          </MetricButton>

          <MetricButton
            $active={selectedMetric === 'publicSentiment'}
            $activeColor="#FFD93D"
            $activeHoverColor="#D6B42F"
            onClick={() => setSelectedMetric('publicSentiment')}
          >
            민심
          </MetricButton>

          <MetricButton
            $active={selectedMetric === 'environment'}
            $activeColor="#00C49F"
            $activeHoverColor="#009377"
            onClick={() => setSelectedMetric('environment')}
          >
            환경
          </MetricButton>
        </ButtonGroup>
        <TurnNumber>통치 기간: {finalTurnNumber}턴</TurnNumber>
      </SectionHeader>

      <Section>
        <ChartWrapper style={{ overflowX: 'auto', width: '100%' }}>
          <div
            style={{ minWidth: `${filteredSeries[0]?.points.length * 50}px` }}
          >
            <ReportLineChart
              width={filteredSeries[0]?.points.length * 50}
              height={350}
              series={filteredSeries}
              onClickTurn={(turn) => setSelectedTurn(turn)}
            />
          </div>
        </ChartWrapper>
      </Section>
      {turnDetail && (
        <DetailSection>
          <DetailHeader>
            {turnDetail.data.context.turnNumber}턴 상세 정보
          </DetailHeader>

          <NpcWrapper>
            <Label>등장 NPC!</Label>
            <NpcIcon src={turnDetail.data.card.npc.imageUrl}></NpcIcon>
            <NpcTextBox>
              <NpcName>{turnDetail.data.card.npc.name}</NpcName>
              <NpcText>{turnDetail.data.card.content}</NpcText>
            </NpcTextBox>
          </NpcWrapper>

          <ChoiceWrapper>
            <Label>당신의 선택!</Label>
            <ChoiceCards>
              <ChoiceCard type="A" choiceCode={choiceCode}>
                {turnDetail.data.card.choices[0].label}
              </ChoiceCard>
              <ChoiceCard type="B" choiceCode={choiceCode}>
                {turnDetail.data.card.choices[1].label}
              </ChoiceCard>
            </ChoiceCards>
          </ChoiceWrapper>

          <GraphWrapper>
            <Label>지표 변화</Label>
            <StatsGraphWrapper style={{ height: 200 }}>
              {Object.keys(turnDetail.data.applied.countryStats.before).map(
                (metric) => {
                  const before =
                    turnDetail.data.applied.countryStats.before[metric]
                  const after =
                    turnDetail.data.applied.countryStats.after[metric]
                  const delta =
                    turnDetail.data.applied.countryStats.delta[metric]

                  const maxVal = Math.max(before, after, 100) // 그래프 비율
                  const beforePercent = (before / maxVal) * 100
                  const afterPercent = (after / maxVal) * 100

                  return (
                    <MetricColumn key={metric}>
                      <Bars>
                        <Bar heightPercent={beforePercent} color="#e0e0e0" />
                        <Bar
                          heightPercent={afterPercent}
                          color={delta >= 0 ? '#00C49F' : '#FF6B6B'}
                        />
                      </Bars>
                      <span>{metricLabels[metric]}</span>
                      <span style={{ fontSize: '12px' }}>
                        {before} → {after}
                      </span>
                    </MetricColumn>
                  )
                },
              )}
            </StatsGraphWrapper>
          </GraphWrapper>

          <ArticleWrapper>
            <Label>연관 뉴스</Label>
            <ArticleTitle>
              {turnDetail.data.card.relatedArticle.title}
            </ArticleTitle>
            <ArticleContent>
              {turnDetail.data.card.relatedArticle.content}
            </ArticleContent>
            <ArticleUrl>
              <a href={turnDetail.data.card.relatedArticle.url} target="_blank">
                원본 기사 보러가기!
              </a>
            </ArticleUrl>
          </ArticleWrapper>
        </DetailSection>
      )}
    </Wrapper>
  )
}
