// 랭킹 관련 API 서비스

export interface RankingItem {
  gameId: number
  countryName: string
  turn: number
  endedAt: string
  order: number
}

export interface RankingResponse {
  status: 'success' | 'error'
  data: RankingItem[] | RankingItem
  message: string
  error: {
    code: string
    details: any
  } | null
}

export interface SingleRankingResponse {
  status: 'success' | 'error'
  data: RankingItem
  message: string
  error: {
    code: string
    details: any
  } | null
}

const API_BASE_URL = '/api/v1'

// 내 랭킹 조회
export const getMyRanking = async (): Promise<RankingItem[]> => {
  const response = await fetch(`${API_BASE_URL}/ranking/me`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
  })

  const data: RankingResponse = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '내 랭킹 조회에 실패했습니다.')
  }

  return Array.isArray(data.data) ? data.data : [data.data]
}

// 상위 N개 랭킹 조회 (통합랭킹)
export const getTopRanking = async (
  topN: number = 100,
): Promise<RankingItem[]> => {
  const response = await fetch(
    `${API_BASE_URL}/public/ranking/top?topN=${topN}`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    },
  )

  const data: RankingResponse = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '랭킹 조회에 실패했습니다.')
  }

  return Array.isArray(data.data) ? data.data : [data.data]
}

// 특정 게임 랭킹 조회
export const getGameRanking = async (gameId: number): Promise<RankingItem> => {
  const response = await fetch(
    `${API_BASE_URL}/public/ranking/gameId/${gameId}`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    },
  )

  const data: SingleRankingResponse = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '게임 랭킹 조회에 실패했습니다.')
  }

  return data.data
}
