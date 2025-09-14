import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useGameStore } from '@/store/gameStore';
import { useAuthStore } from '@/store/authStore';
import { useAudio } from '@/hooks/useAudio';
import { useGame } from '@/hooks/useGame';
import { GameBackground } from '@/components/common/GameBackground';
import { ContinueModal } from '@/components/ContinueModal/ContinueModal';
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
  BackButton,
} from '@/routes/game/setup/-GameSetup.styles';

export const Route = createFileRoute('/game/setup/')({
  component: GameSetupPage,
});

function GameSetupPage() {
  const navigate = useNavigate();
  const { setGameStart } = useGameStore();
  const { user } = useAuthStore();
  const { playClickSound } = useAudio();
  const { fetchOngoingGame, fetchGameById, createNewGame } = useGame();

  const [countryName, setCountryName] = useState('');
  const [showNameInput, setShowNameInput] = useState(false);
  const [ongoingGame, setOngoingGame] = useState<any>(null);

  useEffect(() => {
    const checkGame = async () => {
      try {
        const data = await fetchOngoingGame();
        if (data?.data?.game) {
          setOngoingGame(data.data.game);
        } else {
          setShowNameInput(true);
        }
      } catch (err) {
        console.error(err);
        setShowNameInput(true);
      }
    };
    checkGame();
  }, []);

  const startGame = (game: any) => {
    const turn = game.turn;
    setGameStart(
      game.gameId,
      {
        eco: turn.countryStats.eco,
        mil: turn.countryStats.mil,
        opi: turn.countryStats.opi,
        env: turn.countryStats.env,
      },
      game.countryName,
      user?.nickname || '플레이어',
      turn.number
    );
    navigate({ to: '/game' });
  };

  const handleContinue = async () => {
    playClickSound();
    if (!ongoingGame) return;
    const data = await fetchGameById(ongoingGame.gameId);
    if (data?.data?.game) {
      startGame(data.data.game);
    }
  };

  const handleNewGame = () => {
    playClickSound();
    setOngoingGame(null);
    setShowNameInput(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    playClickSound();

    if (!countryName.trim()) {
      alert('나라 이름을 입력해주세요.');
      return;
    }

    try {
      const data = await createNewGame(countryName.trim());
      if (data?.data?.game) {
        startGame(data.data.game);
      }
    } catch (err) {
      console.error(err);
    }
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
      <BackButton onClick={handleBack}>← 뒤로가기</BackButton>

      {ongoingGame && !showNameInput && (
        <ContinueModal
          countryName={ongoingGame.countryName}
          onContinue={handleContinue}
          onNewGame={handleNewGame}
        />
      )}

      {showNameInput && (
        <>
          <TitleSection>
            <TitleText>
              <p>통치할 나라의 이름을 입력해주세요</p>
            </TitleText>
          </TitleSection>

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
              <button type="submit" style={{ display: 'none' }}>
                제출
              </button>
            </CountryNameForm>
          </FormSection>

          <InstructionText>
            <p>Enter를 눌러 계속하기</p>
          </InstructionText>
        </>
      )}
    </SetupContainer>
  );
}
