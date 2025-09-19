import { useState } from 'react';
import { createFileRoute } from '@tanstack/react-router';
import {
  TestContainer,
  TestHeader,
  TestTitle,
  TestControls,
  TestButton,
  MockGameContainer,
  MockBackgroundImage,
  MockEventIcon,
} from '@/routes/test/-Test.styles';

import Parameter from "@/components/Parameter";
import GuestDialog from '@/components/GuestDialog/GuestDialog';
import ChoiceDialog from '@/components/ChoiceDialog/ChoiceDialog.tsx';
import FeedbackDialog from '@/components/FeedbackDialog/FeedbackDialog';

export const Route = createFileRoute('/test/')({
  component: TestComponent,
});

function TestComponent() {
  const [guestOpen, setGuestOpen] = useState(false);
  const [choiceOpen, setChoiceOpen] = useState(false);
  const [feedbackOpen, setFeedbackOpen] = useState(false);
  const [clickPos, setClickPos] = useState<{ x: number; y: number } | null>(null);

  // Mock 게임 데이터
  const mockStats = {
    eco: 65,
    mil: 45,
    opi: 75,
    env: 55
  };

  const mockCard = {
    cardId: "test-card-1",
    content: "새로운 경제 정책을 도입하려고 합니다. 이 정책은 단기적으로는 비용이 들지만 장기적으로는 경제 성장에 도움이 될 것으로 예상됩니다.",
    npc: {
      name: "재정경제부 장관",
      imageUrl: "/icons/npc1.png"
    },
    choices: [
      {
        code: "A",
        label: "정책 즉시 도입"
      },
      {
        code: "B",
        label: "점진적 도입"
      }
    ],
    relatedArticle: {
      title: "정부, 새로운 경제정책 발표",
      url: "https://example.com/news/1",
      content: "정부가 새로운 경제정책을 발표했습니다. 이 정책은 중소기업 지원과 일자리 창출을 목표로 하고 있습니다."
    }
  };

  const handleGuestSelect = (e: React.MouseEvent<HTMLButtonElement>) => {
    setClickPos({ x: e.clientX, y: e.clientY });
    setGuestOpen(false);
    setChoiceOpen(true);
  };

  const handleChoice = (choiceCode: string) => {
    console.log('Selected choice:', choiceCode);
    setChoiceOpen(false);
    setFeedbackOpen(true);
  };

  const resetDialogs = () => {
    setGuestOpen(false);
    setChoiceOpen(false);
    setFeedbackOpen(false);
    setClickPos(null);
  };

  return (
    <TestContainer>
      <TestHeader>
        <TestTitle>GuestDialog 테스트 페이지</TestTitle>
        <TestControls>
          <TestButton onClick={() => setGuestOpen(true)}>
            Guest Dialog 열기
          </TestButton>
          <TestButton onClick={() => setChoiceOpen(true)}>
            Choice Dialog 열기
          </TestButton>
          <TestButton onClick={() => setFeedbackOpen(true)}>
            Feedback Dialog 열기
          </TestButton>
          <TestButton onClick={resetDialogs} variant="reset">
            모든 Dialog 닫기
          </TestButton>
        </TestControls>
      </TestHeader>

      {/* 게임 화면과 동일한 배경 */}
      <MockGameContainer>
        <MockBackgroundImage src="/backgrounds/game_background.png" />

        {/* 말풍선 아이콘 */}
        <MockEventIcon
          src="/icons/말풍선.png"
          onClick={() => setGuestOpen(true)}
        />

        {/* 파라미터들 */}
        <Parameter type="eco" value={mockStats.eco} x={10} y={53} />
        <Parameter type="env" value={mockStats.env} x={29} y={54} />
        <Parameter type="opi" value={mockStats.opi} x={72} y={54} />
        <Parameter type="mil" value={mockStats.mil} x={90} y={54} />
      </MockGameContainer>

      {/* Dialogs */}
      {guestOpen && (
        <GuestDialog
          guestName={mockCard.npc.name}
          guestText={mockCard.content}
          guestImage={mockCard.npc.imageUrl}
          open
          onClose={() => setGuestOpen(false)}
          onSelect={handleGuestSelect}
        />
      )}

      {choiceOpen && (
        <ChoiceDialog
          guestText={mockCard.content}
          choices={mockCard.choices}
          currentStats={mockStats}
          open
          initialMousePos={clickPos}
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
          article={mockCard.relatedArticle}
          onClose={() => setFeedbackOpen(false)}
        />
      )}
    </TestContainer>
  );
}