import { create } from "zustand";
import { loginApi, signupApi, logoutApi, getCurrentUser } from "@/services/authApi";

export interface User {
  id: string;
  email: string;
  nickname: string;
  age?: string;
  gender?: string;
  role?: string;
}

interface AuthState {
  user: User | null;
  isLoading: boolean;
  
  // 액션 함수들
  initializeAuth: () => void;
  login: (email: string, password: string) => Promise<boolean>;
  signup: (email: string, password: string, nickname: string) => Promise<boolean>;
  logout: () => Promise<void>;
  clearUser: () => void;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isLoading: false,

  // 앱 시작시 쿠키에서 사용자 정보 복원
  initializeAuth: () => {
    const user = getCurrentUser();
    if (user) {
      set({ user });
    }
  },

  // 로그인 함수
  login: async (email: string, password: string) => {
    set({ isLoading: true });
    
    try {
      const response = await loginApi({ email, password });
      
      if (response.status === 'success' && response.data) {
        const user: User = {
          id: response.data.id,
          email: response.data.email,
          nickname: response.data.nickname,
          age: response.data.age,
          gender: response.data.gender,
          role: response.data.role,
        };
        
        set({ user, isLoading: false });
        return true;
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
        const user: User = {
          id: response.data.id,
          email: response.data.email,
          nickname: response.data.nickname,
        };
        
        set({ user, isLoading: false });
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