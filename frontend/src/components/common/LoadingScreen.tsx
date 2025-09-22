import React from 'react'
import { DotLottieReact } from '@lottiefiles/dotlottie-react'
import { GameBackground } from '@/components/common/GameBackground'
import { useImageCache } from '@/hooks/useImageCache'
import {
  LoadingContainer,
  LoadingContent,
  LoadingTitle,
  LottieContainer,
  FallbackBackground,
} from './LoadingScreen.styles'

export const LoadingScreen: React.FC = () => {
  const { isImageCached, isChecking } = useImageCache(
    '/backgrounds/background.jpg',
  )

  return (
    <LoadingContainer>
      {/* 이미지가 캐시된 경우에만 GameBackground 사용, 아니면 폴백 배경 */}
      {isImageCached && !isChecking ? (
        <GameBackground />
      ) : (
        <FallbackBackground />
      )}

      <LoadingContent>
        <LoadingTitle>뉴토피아 로딩 중...</LoadingTitle>
        <LottieContainer>
          <DotLottieReact
            src="https://lottie.host/ab4cc7b7-7649-481c-851d-f3b389a6682a/xUeKlSsHWR.lottie"
            loop
            autoplay
            style={{ width: '300px', height: '100px' }}
          />
        </LottieContainer>
      </LoadingContent>
    </LoadingContainer>
  )
}
