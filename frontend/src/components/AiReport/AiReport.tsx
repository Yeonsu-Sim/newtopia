import {
  Section,
  SectionTitle,
  Wrapper,
  Label,
} from '@/components/AiReport/AiReport.styles'

// interface Props {
//   summary?: {
//     status: string;
//     sections?: Record<string, { bullets: string[] }>;
//   };
// }

export default function AiReport() {
  return (
    <Wrapper>
      <SectionTitle>AI 리포트</SectionTitle>
      <Section>
        <Label>준비중입니다!</Label>
      </Section>
    </Wrapper>
  )
}
