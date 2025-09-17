import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useAuthStore } from '@/store/authStore';
import { useAudio } from '@/hooks/useAudio';
import { GameBackground } from '@/components/common/GameBackground';
import { MenuButton } from '@/components/common/MenuButton';
import { HotTopic } from '@/components/common/HotTopic';
import { RankingModal } from '@/components/RankingModal';
import { SuggestionModal } from '@/components/SuggestionModal';
import {
  MainContainer,
  WelcomeSection,
  WelcomeTitle,
  WelcomeSubtitle,
  MenuContainer,
  BgmToggleButton,
  LogoutButton
} from '@/routes/main/-Main.styles';

export const Route = createFileRoute('/main/')({
  component: MainPage,
});

function MainPage() {
  const { user, logout, isLoading } = useAuthStore();
  const navigate = useNavigate();
  const { isBgmPlaying, playClickSound, toggleBgm } = useAudio({
    bgmVolume: 0.5,
    clickSoundVolume: 0.7
  });
  const [showRankingModal, setShowRankingModal] = useState(false);
  const [showSuggestionModal, setShowSuggestionModal] = useState(false);

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
    setShowSuggestionModal(true);
  };

  const handleRanking = () => {
    playClickSound();
    setShowRankingModal(true);
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

      {/* BGM 토글 버튼 */}
      <BgmToggleButton onClick={toggleBgm}>
        {isBgmPlaying ? '🔊' : '🔇'}
      </BgmToggleButton>

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

        <MenuButton onClick={handleRanking} variant="main">
          랭킹
        </MenuButton>
      </MenuContainer>

      {/* 이달의 핫토픽 뉴스 */}
      <HotTopic />

      {/* 랭킹 모달 */}
      <RankingModal
        isOpen={showRankingModal}
        onClose={() => setShowRankingModal(false)}
      />

      {/* 건의사항 모달 */}
      <SuggestionModal
        isOpen={showSuggestionModal}
        onClose={() => setShowSuggestionModal(false)}
      />
    </MainContainer>
  );
}