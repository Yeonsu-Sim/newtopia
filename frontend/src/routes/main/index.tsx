import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { useAudio } from '@/hooks/useAudio';
import { GameBackground } from '@/components/common/GameBackground';
import { MenuButton } from '@/components/common/MenuButton';
import {
  MainContainer,
  WelcomeSection,
  WelcomeTitle,
  WelcomeSubtitle,
  MenuContainer,
  LogoutButton
} from './-Main.styles';

export const Route = createFileRoute('/main/')({
  component: MainPage,
});

function MainPage() {
  const { user, logout, isLoading } = useAuthStore();
  const navigate = useNavigate();
  const { playClickSound } = useAudio();

  // 로그인되지 않은 사용자는 랜딩 페이지로 리다이렉트
  useEffect(() => {
    if (!user) {
      navigate({ to: '/landing' });
    }
  }, [user, navigate]);

  const handleStartGame = () => {
    playClickSound();
    navigate({ to: '/game/setup' });
  };

  const handleMyInfo = () => {
    playClickSound();
    // TODO: 내정보 페이지 구현 후 연결
    alert('내정보 기능은 준비 중입니다.');
  };

  const handleSuggestion = () => {
    playClickSound();
    // TODO: 건의사항 페이지 구현 후 연결
    alert('건의하기 기능은 준비 중입니다.');
  };

  const handleLogout = async () => {
    playClickSound();
    try {
      await logout();
      navigate({ to: '/landing' });
    } catch (error) {
      console.error('로그아웃 중 오류 발생:', error);
    }
  };

  // 사용자 정보가 없으면 로딩 상태 표시
  if (!user) {
    return (
      <MainContainer>
        <GameBackground />
        <WelcomeSection>
          <WelcomeTitle>로딩 중...</WelcomeTitle>
        </WelcomeSection>
      </MainContainer>
    );
  }

  return (
    <MainContainer>
      <GameBackground />
      
      {/* 로그아웃 버튼 */}
      <LogoutButton onClick={handleLogout} disabled={isLoading}>
        {isLoading ? '처리 중...' : '로그아웃'}
      </LogoutButton>

      {/* 환영 메시지 */}
      <WelcomeSection>
        <WelcomeTitle>환영합니다, {user.nickname}님!</WelcomeTitle>
        <WelcomeSubtitle>뉴토피아에서 당신만의 통치를 시작해보세요</WelcomeSubtitle>
      </WelcomeSection>
      
      {/* 메뉴 버튼들 */}
      <MenuContainer>
        <MenuButton onClick={handleStartGame} variant="main">
          게임하기
        </MenuButton>

        <MenuButton onClick={handleMyInfo} variant="main">
          내정보
        </MenuButton>

        <MenuButton onClick={handleSuggestion} variant="main">
          건의하기
        </MenuButton>
      </MenuContainer>
    </MainContainer>
  );
}