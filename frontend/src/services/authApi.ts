// 인증 관련 API 서비스

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  status: string;
  data: {
    id: string;
    email: string;
    nickname: string;
    age?: string;
    gender?: string;
    role: string;
  };
  message: string;
  error: null | {
    code: string;
    details: null;
  };
}

export interface SignupRequest {
  email: string;
  password: string;
  nickname: string;
}

export interface SignupResponse {
  status: string;
  data: {
    id: string;
    email: string;
    nickname: string;
    created_at: string;
  };
  message: string;
  error: null | {
    code: string;
    details: null;
  };
}

export interface ApiError {
  status: string;
  data: null;
  message: string;
  error: {
    code: string;
    details: null;
  };
}

const API_BASE_URL = '/api/v1';

// 쿠키 값 추출 유틸리티
export const getCookie = (name: string): string | null => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(';').shift() || null;
  return null;
};

// 로그인 API
export const loginApi = async (loginData: LoginRequest): Promise<LoginResponse> => {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include', // HttpOnly 쿠키 처리
    body: JSON.stringify(loginData),
  });

  const data = await response.json();
  
  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '로그인에 실패했습니다.');
  }

  return data;
};

// 회원가입 API
export const signupApi = async (signupData: SignupRequest): Promise<SignupResponse> => {
  const response = await fetch(`${API_BASE_URL}/auth/signup`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include', // HttpOnly 쿠키 처리
    body: JSON.stringify(signupData),
  });

  const data = await response.json();
  
  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '회원가입에 실패했습니다.');
  }

  return data;
};

// 로그아웃 API
export const logoutApi = async (): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/auth/logout`, {
    method: 'POST',
    credentials: 'include', // HttpOnly 쿠키 처리
  });

  if (!response.ok) {
    throw new Error('로그아웃에 실패했습니다.');
  }
};

// 현재 로그인 상태 확인
export const getCurrentUser = () => {
  const memberId = getCookie('memberId');
  const email = getCookie('email');
  const nickname = getCookie('nickname');

  if (memberId && email && nickname) {
    return {
      id: memberId,
      email: email,
      nickname: nickname,
    };
  }

  return null;
};