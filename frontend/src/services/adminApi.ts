// 관리자 관련 API 서비스 (기존 authApi.ts 패턴 따름)

export interface AdminVerifyResponse {
  status: 'success' | 'error'
  data: {
    id: number
    email: string
    nickname: string
    role: string
  } | null
  message: string
  error: {
    code: string
    details: any
  } | null
}

export interface AdminStatsResponse {
  status: 'success' | 'error'
  data: {
    totalCount: number
    typeStats?: any
    categoryStats?: any
    recentCount: number
  } | null
  message: string
  error: {
    code: string
    details: any
  } | null
}

export interface Notice {
  id: number
  title: string
  content: string
  type: string
  createdAt: string
  updatedAt: string
}

export interface NoticeListResponse {
  status: 'success' | 'error'
  data: Notice[] | null
  message: string
  error: {
    code: string
    details: any
  } | null
}

export interface CreateNoticeRequest {
  title: string
  content: string
  type: string
}

export interface NoticeActionResponse {
  status: 'success' | 'error'
  data: Notice | null
  message: string
  error: {
    code: string
    details: any
  } | null
}

const API_BASE_URL = '/api/v1'

// 관리자 권한 확인 API
export const verifyAdminApi = async (): Promise<AdminVerifyResponse> => {
  const response = await fetch(`${API_BASE_URL}/admin/verify`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include', // HttpOnly 쿠키 처리
  })

  const data = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '관리자 권한 확인에 실패했습니다.')
  }

  return data
}

// 모든 공지사항 조회 API
export const getAllNoticesApi = async (): Promise<NoticeListResponse> => {
  const response = await fetch(`${API_BASE_URL}/notices`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
  })

  const data = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '공지사항 조회에 실패했습니다.')
  }

  return data
}

// 공지사항 통계 조회 API
export const getNoticeStatsApi = async (): Promise<AdminStatsResponse> => {
  const response = await fetch(`${API_BASE_URL}/admin/notices/stats`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
  })

  const data = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '공지사항 통계 조회에 실패했습니다.')
  }

  return data
}

// 공지사항 생성 API
export const createNoticeApi = async (notice: CreateNoticeRequest): Promise<NoticeActionResponse> => {
  const response = await fetch(`${API_BASE_URL}/notices`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify(notice),
  })

  const data = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '공지사항 생성에 실패했습니다.')
  }

  return data
}

// 공지사항 삭제 API
export const deleteNoticeApi = async (id: number): Promise<NoticeActionResponse> => {
  const response = await fetch(`${API_BASE_URL}/notices/${id}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
  })

  const data = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '공지사항 삭제에 실패했습니다.')
  }

  return data
}

// 건의사항 통계 조회 API
export const getSuggestionStatsApi = async (): Promise<AdminStatsResponse> => {
  const response = await fetch(`${API_BASE_URL}/admin/suggestions/stats`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
  })

  const data = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '건의사항 통계 조회에 실패했습니다.')
  }

  return data
}