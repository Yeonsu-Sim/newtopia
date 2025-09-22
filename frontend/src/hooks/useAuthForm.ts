import { useState } from 'react'
import { useAuthStore } from '@/store/authStore'

export type AuthMode = 'login' | 'signup'

interface FormData {
  email: string
  password: string
  nickname: string
  confirmPassword: string
}

export const useAuthForm = (
  initialMode: AuthMode = 'login',
  initialEmail?: string,
  initialPassword?: string,
) => {
  const [mode, setMode] = useState<AuthMode>(initialMode)
  const [formData, setFormData] = useState<FormData>({
    email: initialEmail || '',
    password: initialPassword || '',
    nickname: '',
    confirmPassword: '',
  })
  const [error, setError] = useState<string>('')

  const { login, signup, isLoading } = useAuthStore()

  // 폼 데이터 업데이트
  const handleInputChange = (field: keyof FormData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
  }

  // 폼 초기화
  const resetForm = () => {
    setFormData({
      email: '',
      password: '',
      nickname: '',
      confirmPassword: '',
    })
    setError('')
  }

  // 회원가입 유효성 검사
  const validateSignup = (): string | null => {
    if (!formData.nickname.trim()) {
      return '닉네임을 입력해주세요.'
    }
    if (formData.password.length < 6) {
      return '비밀번호는 6자 이상이어야 합니다.'
    }
    if (formData.password !== formData.confirmPassword) {
      return '비밀번호가 일치하지 않습니다.'
    }
    return null
  }

  // 폼 제출 처리
  const handleSubmit = async (
    onSuccess?: () => void,
    onSignupSuccess?: (email: string, password: string) => void,
  ) => {
    setError('')

    try {
      if (mode === 'login') {
        const success = await login(formData.email, formData.password)
        if (success) {
          resetForm()
          onSuccess?.()
        }
      } else {
        const validationError = validateSignup()
        if (validationError) {
          setError(validationError)
          return
        }

        const success = await signup(
          formData.email,
          formData.password,
          formData.nickname,
        )
        if (success) {
          // 회원가입 성공 시 이메일, 비밀번호 정보를 전달하고 폼은 초기화하지 않음
          onSignupSuccess?.(formData.email, formData.password)
        }
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '오류가 발생했습니다.')
    }
  }

  // 로그인/회원가입 모드 전환
  const switchMode = () => {
    setMode(mode === 'login' ? 'signup' : 'login')
    resetForm()
  }

  // 로그인 모드로 전환 (회원가입 정보 유지)
  const switchToLogin = (email?: string, password?: string) => {
    setMode('login')
    if (email && password) {
      setFormData((prev) => ({
        ...prev,
        email,
        password,
        nickname: '',
        confirmPassword: '',
      }))
    }
    setError('')
  }

  return {
    mode,
    formData,
    error,
    isLoading,
    handleInputChange,
    handleSubmit,
    switchMode,
    switchToLogin,
    resetForm,
  }
}
