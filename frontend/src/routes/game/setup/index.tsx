import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useState } from 'react';
import { useGameStore } from '@/store/gameStore';
import { useAudio } from '@/hooks/useAudio';
import { GameBackground } from '@/components/common/GameBackground';
import {
  SetupContainer,
  TitleSection,
  TitleText,
  FormSection,
  CountryNameForm,
  InputWrapper,
  InputInner,
  CountryNameInput,
  InstructionText,
  BackButton
} from './-GameSetup.styles';

export const Route = createFileRoute('/game/setup/')({
  component: GameSetupPage,
});

function GameSetupPage() {
  const navigate = useNavigate();
  const { setGameStart } = useGameStore();
  const [countryName, setCountryName] = useState('');
  const { playClickSound } = useAudio();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    playClickSound();
    
    if (!countryName.trim()) {
      alert('나라 이름을 입력해주세요.');
      return;
    }

    // 게임 상태 초기화 및 시작
    // TODO: 실제 게임 시작 API 호출 후 실제 데이터로 교체
    const mockGameId = `game_${Date.now()}`;
    const initialStats = { eco: 50, mil: 50, opi: 50, env: 50 };
    const playerName = "플레이어"; // TODO: 로그인된 사용자 닉네임으로 교체
    const initialTurn = 1;

    setGameStart(
      mockGameId,
      initialStats,
      countryName.trim(),
      playerName,
      initialTurn
    );

    // 게임 화면으로 이동
    navigate({ to: '/game' });
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCountryName(e.target.value);
  };

  const handleBack = () => {
    playClickSound();
    navigate({ to: '/main' });
  };

  return (
    <SetupContainer>
      <GameBackground variant="setup" />
      
      {/* 뒤로가기 버튼 */}
      <BackButton onClick={handleBack}>
        ← 뒤로가기
      </BackButton>
      
      {/* 제목 */}
      <TitleSection>
        <TitleText>
          <p>통치할 나라의 이름을 입력해주세요</p>
        </TitleText>
      </TitleSection>

      {/* 입력 폼 */}
      <FormSection>
        <CountryNameForm onSubmit={handleSubmit}>
          <InputWrapper>
            <InputInner>
              <CountryNameInput
                type="text"
                value={countryName}
                onChange={handleInputChange}
                placeholder="이름"
                maxLength={20}
                required
              />
            </InputInner>
          </InputWrapper>
          
          {/* 숨겨진 제출 버튼 (Enter 키로 제출) */}
          <button type="submit" style={{ display: 'none' }}>제출</button>
        </CountryNameForm>
      </FormSection>

      {/* 안내 텍스트 */}
      <InstructionText>
        <p>Enter를 눌러 계속하기</p>
      </InstructionText>
    </SetupContainer>
  );
}