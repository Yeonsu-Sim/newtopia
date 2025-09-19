import { create } from "zustand";
import { loginApi, signupApi, logoutApi, checkAuthApi } from "@/services/authApi";

export interface User {
  id: string;
  email: string;
  nickname: string;
  role?: string;
}

interface AuthState {
  user: User | null;
  isLoading: boolean;
  isInitialized: boolean; // 초기화 완료 여부

  // 액션 함수들
  initializeAuth: () => Promise<void>;
  login: (email: string, password: string) => Promise<boolean>;
  signup: (email: string, password: string, nickname: string) => Promise<boolean>;
  logout: () => Promise<void>;
  clearUser: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isLoading: false,
  isInitialized: false,

  // 앱 시작시 서버에서 로그인 상태 확인
  initializeAuth: async () => {
    set({ isLoading: true });

    try {
      const response = await checkAuthApi();

      if (response.status === 'success' && response.data && response.data.id) {
        const user: User = {
          id: String(response.data.id),
          email: response.data.email,
          nickname: response.data.nickname,
          role: response.data.role,
        };

        set({ user, isLoading: false, isInitialized: true });
      } else {
        // 로그인되지 않은 상태
        set({ user: null, isLoading: false, isInitialized: true });
      }
    } catch (error) {
      console.error('로그인 상태 확인 실패:', error);
      // 에러 발생 시에도 초기화 완료로 처리
      set({ user: null, isLoading: false, isInitialized: true });
    }
  },

  // 로그인 함수
  login: async (email: string, password: string) => {
    set({ isLoading: true });

    try {
      const response = await loginApi({ email, password });

      if (response.status === 'success') {
        // 로그인 성공 후 사용자 정보를 가져오기 위해 checkAuthApi 호출
        const userResponse = await checkAuthApi();

        if (userResponse.status === 'success' && userResponse.data && userResponse.data.id) {
          const user: User = {
            id: String(userResponse.data.id),
            email: userResponse.data.email,
            nickname: userResponse.data.nickname,
            role: userResponse.data.role,
          };

          set({ user, isLoading: false });
          return true;
        }
      }

      set({ isLoading: false });
      return false;
    } catch (error) {
      set({ isLoading: false });
      throw error;
    }
  },

  // 회원가입 함수
  signup: async (email: string, password: string, nickname: string) => {
    set({ isLoading: true });

    try {
      const response = await signupApi({ email, password, nickname });

      if (response.status === 'success' && response.data) {
        // 회원가입 성공 시 자동 로그인하지 않음
        set({ isLoading: false });
        return true;
      }

      set({ isLoading: false });
      return false;
    } catch (error) {
      set({ isLoading: false });
      throw error;
    }
  },

  // 로그아웃 함수
  logout: async () => {
    set({ isLoading: true });
    
    try {
      await logoutApi();
      set({ user: null, isLoading: false });
    } catch (error) {
      // 로그아웃은 실패해도 로컬 상태는 초기화
      set({ user: null, isLoading: false });
      throw error;
    }
  },

  // 사용자 정보 초기화 (에러시 사용)
  clearUser: () => {
    set({ user: null, isLoading: false });
  },
}));