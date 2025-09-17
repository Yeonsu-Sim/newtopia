import { useEffect, useState } from 'react';
import { createFileRoute, useNavigate, useRouter } from '@tanstack/react-router';
import {
  Container,
  TopRightButton,
  EndingImage,
  EndingText
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
      <TopRightButton onClick={() => navigate({ to: '/' })}>
        메인으로
      </TopRightButton>
      <EndingImage 
        // TODO: minIO 해결되면 수정
        // src={ending.assets.imageUrl} 
        src={`/ending/${ending.code}.png`}
        alt={ending.code} />
      <EndingText>
        <h2>{ending.content}</h2>
      </EndingText>
    </Container>
  );
}
