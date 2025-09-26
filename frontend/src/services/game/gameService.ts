// 지표 변동 크기 타입 정의
export type ParameterChangeLevel = 'none' | 'small' | 'medium' | 'large';

// 개별 선택지의 지표 변동 힌트
export interface ChoiceHint {
  economy: ParameterChangeLevel;
  defense: ParameterChangeLevel;
  environment: ParameterChangeLevel;
  publicSentiment: ParameterChangeLevel;
}

// 힌트 API 응답 데이터
export interface HintData {
  A: ChoiceHint;
  B: ChoiceHint;
}

// 힌트 API 전체 응답
export interface HintApiResponse {
  status: 'success' | 'error';
  data: HintData;
  message: string;
  error: {
    code: string;
    details: {};
  } | null;
}

const API_BASE_URL = '/api/v1'

export const gameService = {
  fetchOngoingGame: async () => {
    const res = await fetch(`${API_BASE_URL}/games/me`, {
      credentials: 'include',
    })
    if (!res.ok) throw new Error('진행 중 게임 조회  실패')
    return await res.json()
  },

  fetchGameById: async (gameId: number) => {
    const res = await fetch(`${API_BASE_URL}/games/${gameId}`, {
      credentials: 'include',
    })
    if (!res.ok) throw new Error('게임 불러오기 실패')
    return await res.json()
  },

  createNewGame: async (countryName: string) => {
    const res = await fetch(`${API_BASE_URL}/games?force=true`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ countryName }),
    })
    if (!res.ok) throw new Error('게임 생성 실패')
    return await res.json()
  },

  // 선택지 힌트 조회
  fetchChoiceHints: async (gameId: number): Promise<HintData> => {
    try {
      const res = await fetch(`${API_BASE_URL}/games/${gameId}/hints`, {
        method: 'GET',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
      })

      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`)
      }

      const data: HintApiResponse = await res.json()

      if (data.status === 'error') {
        throw new Error(data.message || '힌트를 불러오는데 실패했습니다.')
      }

      return data.data
    } catch (error) {
      console.error('Failed to fetch choice hints:', error)
      throw error instanceof Error ? error : new Error('힌트를 불러오는데 실패했습니다.')
    }
  },
}

// 지표 변동 크기를 숫자 범위로 변환하는 유틸리티 함수
export const getChangeRange = (level: ParameterChangeLevel): string => {
  switch (level) {
    case 'none':
      return '0'
    case 'small':
      return '1~10'
    case 'medium':
      return '11~20'
    case 'large':
      return '21+'
    default:
      return '0'
  }
}

// 지표 변동 크기를 아이콘으로 변환하는 유틸리티 함수
export const getChangeIcon = (level: ParameterChangeLevel): string => {
  switch (level) {
    case 'none':
      return '○'
    case 'small':
      return '△'
    case 'medium':
      return '▲'
    case 'large':
      return '◆'
    default:
      return '○'
  }
}

// 지표 변동 크기를 색상으로 변환하는 유틸리티 함수
export const getChangeColor = (level: ParameterChangeLevel): string => {
  switch (level) {
    case 'none':
      return '#666666'
    case 'small':
      return '#4CAF50'
    case 'medium':
      return '#FF9800'
    case 'large':
      return '#F44336'
    default:
      return '#666666'
  }
}
