const API_BASE_URL = '/api/v1';

export const gameService = {
  fetchOngoingGame: async () => {
    const res = await fetch(`${API_BASE_URL}/games/me`, { credentials: 'include' });
    if (!res.ok) throw new Error('진행 중 게임 조회  실패');
    return await res.json();
  },

  fetchGameById: async (gameId: number) => {
    const res = await fetch(`${API_BASE_URL}/games/${gameId}`, { credentials: 'include' });
    if (!res.ok) throw new Error('게임 불러오기 실패');
    return await res.json();
  },

  createNewGame: async (countryName: string) => {
    const res = await fetch(`${API_BASE_URL}/games?force=true`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ countryName }),
    });
    if (!res.ok) throw new Error('게임 생성 실패');
    return await res.json();
  },
};
