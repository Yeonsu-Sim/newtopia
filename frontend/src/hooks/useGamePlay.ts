import { gamePlayService } from '@/services/gamePlay/gamePlayService'

export const useGamePlay = () => {
  const submitChoice = async (
    gameId: number,
    cardId: string,
    choice: 'A' | 'B',
  ) => {
    const result = await gamePlayService.submitChoice(gameId, cardId, choice)
    console.log('[submitChoice]', result)

    if (result?.data?.gameState?.gameOver) {
      return {
        gameOver: true,
        ending: result.data.gameState.ending,
      }
    }

    return {
      gameOver: false,
      nextTurn: result?.data?.nextTurn,
    }
  }

  return { submitChoice }
}
