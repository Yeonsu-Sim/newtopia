import { useEffect, useState } from 'react';
import { createFileRoute, useNavigate, useRouter } from '@tanstack/react-router';
import { useAudio } from '@/hooks/useAudio';
import {
  Container,
  TopRightButton,
  EndingImage,
  EndingText,
  NextButton,
  ButtonGroup
} from '@/routes/ending/-Ending.styles';

export const Route = createFileRoute('/ending/')({
  component: RouteComponent,
});

function RouteComponent() {
  const navigate = useNavigate();
  const router = useRouter();
  const searchParams = new URLSearchParams(router.state.location.search);
  const endingCode = searchParams.get('endingCode');

  const [ending, setEnding] = useState<any>(null);
  const [fadeIn, setFadeIn] = useState(false);
  const { playClickSound } = useAudio({ enableBgm: false });

useEffect(() => {
  const fetchEnding = async () => {
    try {
      const res = await fetch(`/api/v1/endings/${endingCode}`, {
        credentials: 'include',
      });
      if (!res.ok) throw new Error('엔딩 불러오기 실패');
      const data = await res.json();
      console.log(data.data);
      setEnding(data.data);
      
      // 게임 오버 사운드 재생
      const gameOverSound = new Audio('/sounds/game-over-arcade-6435.mp3');
      gameOverSound.volume = 0.7;
      gameOverSound.play().catch(console.error);
      
      // 페이드인 애니메이션 시작
      setTimeout(() => {
        setFadeIn(true);
      }, 300);
    } catch (err) {
      console.error(err);
    }
  };

  if (endingCode) {
    fetchEnding();
  }
}, [endingCode]);

  if (!ending) return <Container>로딩 중...</Container>;

  return (
    <Container>
      <EndingImage 
        src={ending.assets.imageUrl}
        alt={ending.code}
        fadeIn={fadeIn}
      />
      <EndingText fadeIn={fadeIn}>
        <h2>{ending.content}</h2>
      </EndingText>

      <ButtonGroup>
        <NextButton onClick={() => {
          playClickSound();
          navigate({ to: '/report' });
        }}>
          리포트 보러가기
        </NextButton>
        <TopRightButton onClick={() => {
          playClickSound();
          navigate({ to: '/' });
        }}>
          메인으로
        </TopRightButton>
      </ButtonGroup>
    </Container>
  );
}
