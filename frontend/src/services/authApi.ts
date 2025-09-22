// 인증 관련 API 서비스

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  status: string
  data: {
    id: string
    email: string
    nickname: string
    role: string
  }
  message: string
  error: null | {
    code: string
    details: null
  }
}

export interface SignupRequest {
  email: string
  password: string
  nickname: string
}

export interface SignupResponse {
  status: string
  data: {
    id: string
    email: string
    nickname: string
    created_at: string
  }
  message: string
  error: null | {
    code: string
    details: null
  }
}

export interface ApiError {
  status: string
  data: null
  message: string
  error: {
    code: string
    details: null
  }
}

const API_BASE_URL = '/api/v1'

// 로그인 API
export const loginApi = async (
  loginData: LoginRequest,
): Promise<LoginResponse> => {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include', // HttpOnly 쿠키 처리
    body: JSON.stringify(loginData),
  })

  const data = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '로그인에 실패했습니다.')
  }

  return data
}

// 회원가입 API
export const signupApi = async (
  signupData: SignupRequest,
): Promise<SignupResponse> => {
  const response = await fetch(`${API_BASE_URL}/auth/signup`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include', // HttpOnly 쿠키 처리
    body: JSON.stringify(signupData),
  })

  const data = await response.json()

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '회원가입에 실패했습니다.')
  }

  return data
}

// 로그아웃 API
export const logoutApi = async (): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/auth/logout`, {
    method: 'POST',
    credentials: 'include', // HttpOnly 쿠키 처리
  })

  if (!response.ok) {
    throw new Error('로그아웃에 실패했습니다.')
  }
}

// 로그인 확인 API 응답 타입
export interface CheckAuthResponse {
  status: 'success' | 'error'
  data: {
    id: number
    email: string
    nickname: string
    age?: number
    gender?: 'MALE' | 'FEMALE'
    role?: 'ADMIN' | 'USER'
  } | null
  message: string
  error?: {
    code: string
    details: object
  }
}

// 서버에서 로그인 상태 확인
export const checkAuthApi = async (): Promise<CheckAuthResponse> => {
  const response = await fetch(`${API_BASE_URL}/public/me`, {
    method: 'GET',
    credentials: 'include', // HttpOnly 쿠키 처리
  })

  const data = await response.json()
  return data // 항상 200을 반환하므로 에러 처리 불필요
}
