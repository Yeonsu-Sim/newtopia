import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useState, useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { useAudio } from '@/hooks/useAudio';
import { AuthModal } from '@/components/AuthModal/AuthModal';
import { GameBackground } from '@/components/common/GameBackground';
import { MenuButton } from '@/components/common/MenuButton';
import {
  LandingContainer,
  GameLogoSection,
  GameLogo,
  PressStartButton,
  MenuContainer,
  BgmToggleButton,
  WelcomeSection,
  WelcomeTitle,
  WelcomeSubtitle
} from '@/routes/landing/-Landing.styles';

export const Route = createFileRoute('/landing/')({
  component: LandingPage,
});

function LandingPage() {
  const { user } = useAuthStore();
  const navigate = useNavigate();
  const [currentState, setCurrentState] = useState<'initial' | 'menu'>('initial');
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [authMode] = useState<'login' | 'signup'>('login');
  
  // 오디오 커스텀 훅 사용
  const { isBgmPlaying, playClickSound, toggleBgm } = useAudio({
    bgmVolume: 0.5,
    clickSoundVolume: 0.7
  });


  // 로그인된 사용자는 메인 페이지로 리다이렉트
  useEffect(() => {
    if (user) {
      navigate({ to: '/main' });
    }
  }, [user, navigate]);

  const handlePressStart = () => {
    playClickSound();
    setCurrentState('menu');
  };
  
  const handleTryGame = () => {
    playClickSound();
    alert('체험하기 기능은 현재 준비중입니다.');
  };
  
  const handleShowAuth = () => {
    playClickSound();
    setShowAuthModal(true);
  };

  // 초기 랜딩 화면
  if (currentState === 'initial') {
    return (
      <>
        <LandingContainer>
          <GameBackground />

          <BgmToggleButton onClick={toggleBgm}>
            {isBgmPlaying ? '🔊' : '🔇'}
          </BgmToggleButton>

          <GameLogoSection>
            <GameLogo />
            <PressStartButton onClick={handlePressStart}>
              press start
            </PressStartButton>
          </GameLogoSection>
        </LandingContainer>

        <AuthModal
          isOpen={showAuthModal}
          onClose={() => setShowAuthModal(false)}
          initialMode={authMode}
        />
      </>
    );
  }

  // 메뉴 화면
  return (
    <>
      <LandingContainer>
        <GameBackground />

        <BgmToggleButton onClick={toggleBgm}>
          {isBgmPlaying ? '🔊' : '🔇'}
        </BgmToggleButton>

        {/* 환영 메시지 */}
        <WelcomeSection>
          <WelcomeTitle>환영합니다!</WelcomeTitle>
          <WelcomeSubtitle>뉴토피아에서 당신만의 통치를 시작해보세요</WelcomeSubtitle>
        </WelcomeSection>

        <MenuContainer>
          <MenuButton onClick={handleTryGame} variant="landing">
            체험하기
          </MenuButton>

          <MenuButton onClick={handleShowAuth} variant="landing">
            로그인
          </MenuButton>
        </MenuContainer>
      </LandingContainer>

      <AuthModal
        isOpen={showAuthModal}
        onClose={() => setShowAuthModal(false)}
        initialMode={authMode}
      />
    </>
  );
}