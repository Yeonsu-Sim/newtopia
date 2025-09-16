import React from 'react';
import { useAuthForm } from '@/hooks/useAuthForm';
import {
  ModalOverlay,
  ModalBackground,
  ModalContent,
  ModalInner,
  ModalHeader,
  HeaderTitle,
  HeaderSubtitle,
  ModalForm,
  ErrorMessage,
  FormFields,
  InputWrapper,
  Input,
  SubmitButtonWrapper,
  SubmitButton,
  SubmitButtonInner,
  SubmitButtonText,
  ModeSwitch,
  ModeSwitchButton,
  ModalFrame,
  ModalBorder,
  CloseButton
} from './AuthModal.styles';

interface AuthModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialMode?: 'login' | 'signup';
}

export const AuthModal: React.FC<AuthModalProps> = ({
  isOpen,
  onClose,
  initialMode = 'login'
}) => {
  const {
    mode,
    formData,
    error,
    isLoading,
    handleInputChange,
    handleSubmit,
    switchMode,
    switchToLogin
  } = useAuthForm(initialMode);

  if (!isOpen) return null;

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await handleSubmit(
      onClose, // 로그인 성공 시 모달 닫기
      (email: string, password: string) => {
        // 회원가입 성공 시 로그인 모드로 전환하고 정보 자동 입력
        switchToLogin(email, password);
      }
    );
  };

  const handleOverlayClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <ModalOverlay>
      <ModalBackground onClick={handleOverlayClick} />
      
      <ModalContent>
        <ModalInner>
          {/* 헤더 */}
          <ModalHeader>
            <HeaderTitle>
              <p>{mode === 'login' ? 'Newtopia에 오신 것을 환영합니다' : 'Newtopia 회원가입'}</p>
            </HeaderTitle>
            <HeaderSubtitle>
              <p>{mode === 'login' ? '계정으로 로그인하세요.' : '새 계정을 만들어 통치의 여정을 시작하세요.'}</p>
            </HeaderSubtitle>
          </ModalHeader>

          {/* 폼 */}
          <ModalForm onSubmit={onSubmit}>
            {error && <ErrorMessage>{error}</ErrorMessage>}

            <FormFields>
              {/* 닉네임 (회원가입시만) */}
              {mode === 'signup' && (
                <InputWrapper>
                  <Input
                    type="text"
                    value={formData.nickname}
                    onChange={(e) => handleInputChange('nickname', e.target.value)}
                    placeholder="닉네임을 입력해주세요"
                    required
                  />
                </InputWrapper>
              )}

              {/* 이메일 */}
              <InputWrapper>
                <Input
                  type="email"
                  value={formData.email}
                  onChange={(e) => handleInputChange('email', e.target.value)}
                  placeholder="이메일을 입력해주세요"
                  required
                />
              </InputWrapper>

              {/* 비밀번호 */}
              <InputWrapper>
                <Input
                  type="password"
                  value={formData.password}
                  onChange={(e) => handleInputChange('password', e.target.value)}
                  placeholder="비밀번호를 입력해주세요"
                  required
                  minLength={6}
                />
              </InputWrapper>

              {/* 비밀번호 확인 (회원가입시만) */}
              {mode === 'signup' && (
                <InputWrapper>
                  <Input
                    type="password"
                    value={formData.confirmPassword}
                    onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                    placeholder="비밀번호를 다시 입력해주세요"
                    required
                    minLength={6}
                  />
                </InputWrapper>
              )}

              {/* 제출 버튼 */}
              <SubmitButtonWrapper>
                <SubmitButton type="submit" disabled={isLoading}>
                  <SubmitButtonInner>
                    <SubmitButtonText>
                      <p>{isLoading ? '처리 중...' : (mode === 'login' ? '로그인' : '회원가입')}</p>
                    </SubmitButtonText>
                  </SubmitButtonInner>
                </SubmitButton>
              </SubmitButtonWrapper>
            </FormFields>

            {/* 모드 전환 */}
            <ModeSwitch>
              <ModeSwitchButton type="button" onClick={switchMode}>
                {mode === 'login' ? '계정이 없으신가요? 회원가입' : '이미 계정이 있으신가요? 로그인'}
              </ModeSwitchButton>
            </ModeSwitch>
          </ModalForm>
        </ModalInner>

        <ModalFrame />
        <ModalBorder />
        <CloseButton onClick={onClose}>✕</CloseButton>
      </ModalContent>
    </ModalOverlay>
  );
};