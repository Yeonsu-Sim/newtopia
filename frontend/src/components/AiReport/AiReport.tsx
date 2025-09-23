import { useAiReport } from '@/hooks/useAiReport'
import {
  Section,
  SectionTitle,
  Wrapper,
  Label,
  Content
} from '@/components/AiReport/AiReport.styles'

export default function AiReport({ gameId }: { gameId: number }) {
  const { report, loading, error } = useAiReport(gameId)
  const status = report?.status ?? (loading ? 'PENDING' : 'ERROR')

  if (loading) {
    return (
      <Wrapper>
        <SectionTitle>AI 리포트</SectionTitle>
        <Section>
          <Label>⏳ 요약 생성 대기중...</Label>
        </Section>
      </Wrapper>
    )
  }

  if (error || status === 'ERROR') {
    return (
      <Wrapper>
        <SectionTitle>AI 리포트</SectionTitle>
        <Section>
          <Label>❌ {error ?? '리포트 생성 실패!'}</Label>
        </Section>
      </Wrapper>
    )
  }

  if (status === 'PENDING' || status ==='PROCESSING') {
    return (
      <Wrapper>
        <SectionTitle>AI 리포트</SectionTitle>
        <Section>
          <Label>⚙️ 요약 생성중...</Label>
        </Section>
      </Wrapper>
    )
  }

  if (status === 'READY' && report?.sections?.blocks) {
    const { highlights, ending, brief } = report.sections.blocks
    return (
      <Wrapper>
        <SectionTitle>AI 리포트</SectionTitle>

        {highlights && (
          <Section>
            <Label>{highlights.title}</Label>
            {highlights.bullets?.map((b, i) => (
              <Content key={i}>• {b}</Content>
            ))}
          </Section>
        )}

        {ending && (
          <Section>
            <Label>{ending.title}</Label>
            {ending.bullets?.map((b, i) => (
              <Content key={i}>• {b}</Content>
            ))}
          </Section>
        )}

        {brief && (
          <Section>
            <Label>{brief.title}</Label>
            {brief.text && <Content>{brief.text}</Content>}
          </Section>
        )}
      </Wrapper>
    )
  }

  return null
}
