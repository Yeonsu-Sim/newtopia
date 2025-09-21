import { useState, useEffect, useRef } from 'react'

interface UseAudioOptions {
  bgmTracks?: string[]
  enableBgm?: boolean
  bgmVolume?: number
  clickSoundVolume?: number
}

// 전역 BGM 상태 관리
let globalBgmAudio: HTMLAudioElement | null = null
let globalBgmStarted = false
let globalBgmPlaying = true // 기본값을 true로 설정

export const useAudio = (options: UseAudioOptions = {}) => {
  const {
    bgmTracks = [
      '/sounds/Edelstein_City_(NEWTRO Ver.).mp3',
      '/sounds/Newtro_Henesys.mp3',
      '/sounds/Newtro_Kingdom.mp3',
    ],
    enableBgm = true,
    bgmVolume = 0.5,
    clickSoundVolume = 0.7,
  } = options

  const [isBgmPlaying, setIsBgmPlaying] = useState(
    enableBgm && globalBgmPlaying,
  )
  const clickSoundRef = useRef<HTMLAudioElement | null>(null)

  // 전역 상태와 로컬 상태 동기화
  useEffect(() => {
    if (enableBgm) {
      setIsBgmPlaying(globalBgmPlaying)
    }
  }, [enableBgm])

  // 오디오 초기화
  useEffect(() => {
    // BGM 초기화 (enableBgm이 true일 때만, 전역 BGM이 없을 때만)
    if (enableBgm && bgmTracks.length > 0 && !globalBgmAudio) {
      const randomTrack =
        bgmTracks[Math.floor(Math.random() * bgmTracks.length)]
      globalBgmAudio = new Audio(randomTrack)
      globalBgmAudio.loop = true
      globalBgmAudio.volume = bgmVolume

      // BGM 자동 재생 시도
      const playBgm = async () => {
        if (globalBgmAudio && !globalBgmStarted && globalBgmPlaying) {
          try {
            await globalBgmAudio.play()
            globalBgmStarted = true
            setIsBgmPlaying(true)
          } catch (error) {
            // 자동재생이 차단된 경우 사용자 상호작용 대기
            console.log(
              '자동재생이 차단되었습니다. 사용자 상호작용을 기다립니다.',
            )
            const handleFirstInteraction = () => {
              if (globalBgmAudio && globalBgmPlaying) {
                globalBgmAudio.play().catch(console.error)
                globalBgmStarted = true
                setIsBgmPlaying(true)
              }
              document.removeEventListener('click', handleFirstInteraction)
              document.removeEventListener('keydown', handleFirstInteraction)
            }

            document.addEventListener('click', handleFirstInteraction)
            document.addEventListener('keydown', handleFirstInteraction)
          }
        }
      }

      // 즉시 BGM 재생 시도
      playBgm()

      // 정리 함수
      return () => {
        const handleFirstInteraction = () => {}
        document.removeEventListener('click', handleFirstInteraction)
        document.removeEventListener('keydown', handleFirstInteraction)
      }
    }
  }, [enableBgm, bgmTracks, bgmVolume])

  // 클릭 사운드 초기화
  useEffect(() => {
    clickSoundRef.current = new Audio('/sounds/click.mp3')
    clickSoundRef.current.volume = clickSoundVolume

    return () => {
      if (clickSoundRef.current) {
        clickSoundRef.current = null
      }
    }
  }, [clickSoundVolume])

  // 클릭 사운드 재생 함수
  const playClickSound = () => {
    if (clickSoundRef.current) {
      clickSoundRef.current.currentTime = 0
      clickSoundRef.current.play().catch(console.error)
    }
  }

  // BGM 토글 함수
  const toggleBgm = () => {
    if (globalBgmAudio) {
      if (isBgmPlaying) {
        globalBgmAudio.pause()
        globalBgmPlaying = false
        setIsBgmPlaying(false)
      } else {
        globalBgmAudio.play().catch(console.error)
        globalBgmPlaying = true
        globalBgmStarted = true
        setIsBgmPlaying(true)
      }
    }
  }

  return {
    isBgmPlaying,
    playClickSound,
    toggleBgm,
  }
}
