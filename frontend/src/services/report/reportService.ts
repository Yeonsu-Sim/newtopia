const API_BASE_URL = '/api/v1'

export const getReportContext = async (gameId: number) => {
  const res = await fetch(
    `${API_BASE_URL}/game-results/${gameId}/report?parts=context`,
    {
      credentials: 'include',
    },
  )
  if (!res.ok) throw new Error('Context 조회 실패')
  const data = await res.json()
  console.log(data)
  return data
}

export const getReportGraph = async (gameId: number, size: number = 200) => {
  const res = await fetch(
    `${API_BASE_URL}/game-results/${gameId}/report?parts=graph&size=${size}`,
    {
      credentials: 'include',
    },
  )
  if (!res.ok) throw new Error('Graph 조회 실패')
  const data = await res.json()
  console.log(data)
  return data
}

export const getGameResultTurnDetail = async (
  gameId: number,
  turnNumber: number,
) => {
  const res = await fetch(
    `${API_BASE_URL}/game-results/${gameId}/turns/${turnNumber}`,
    {
      credentials: 'include',
    },
  )
  if (!res.ok) throw new Error('턴 상세 조회 실패')
  const data = await res.json()
  console.log(data)
  return data
}
