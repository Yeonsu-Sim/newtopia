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