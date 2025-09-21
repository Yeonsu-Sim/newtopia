import { useState, useEffect } from 'react'

export const useImageCache = (imageSrc: string) => {
  const [isImageCached, setIsImageCached] = useState(false)
  const [isChecking, setIsChecking] = useState(true)

  useEffect(() => {
    const checkImageCache = () => {
      // 이미지 객체 생성하여 캐시 상태 확인
      const img = new Image()

      // 이미지가 이미 로드되어 있다면 즉시 complete 상태
      img.onload = () => {
        setIsImageCached(true)
        setIsChecking(false)
      }

      img.onerror = () => {
        setIsImageCached(false)
        setIsChecking(false)
      }

      // 매우 짧은 시간 후에도 로드되지 않으면 캐시되지 않은 것으로 판단
      const timeout = setTimeout(() => {
        if (!img.complete) {
          setIsImageCached(false)
          setIsChecking(false)
        }
      }, 50) // 50ms 내에 로드되지 않으면 캐시되지 않은 것으로 판단

      img.src = imageSrc

      // 이미 complete 상태라면 캐시된 이미지
      if (img.complete) {
        setIsImageCached(true)
        setIsChecking(false)
        clearTimeout(timeout)
      }

      return () => {
        clearTimeout(timeout)
      }
    }

    checkImageCache()
  }, [imageSrc])

  return { isImageCached, isChecking }
}
