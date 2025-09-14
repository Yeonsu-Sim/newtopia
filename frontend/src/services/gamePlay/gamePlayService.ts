const API_BASE_URL = '/api/v1';

export const gamePlayService = {
  submitChoice: async (gameId: number, cardId: string, choice: 'A' | 'B') => {
    const res = await fetch(`${API_BASE_URL}/games/${gameId}/choice`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ cardId, choice }),
    });

    if (!res.ok) throw new Error('선택 반영 실패');
    return await res.json();
  },
};
