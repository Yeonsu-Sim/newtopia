import { gameService } from '@/services/game/gameService';

export const useGame = () => {
  const fetchOngoingGame = async () => {
    const data = await gameService.fetchOngoingGame();
    console.log('[fetchOngoingGame]', data);
    return data;
  };

  const fetchGameById = async (gameId: number) => {
    const data = await gameService.fetchGameById(gameId);
    console.log('[fetchGameById]', data);
    return data;
  };

  const createNewGame = async (countryName: string) => {
    const data = await gameService.createNewGame(countryName);
    console.log('[createNewGame]', data);
    return data;
  };

  return { fetchOngoingGame, fetchGameById, createNewGame };
};
