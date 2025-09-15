import { useState, useEffect, useRef } from 'react';

interface UseAudioOptions {
  bgmTracks?: string[];
  enableBgm?: boolean;
  bgmVolume?: number;
  clickSoundVolume?: number;
}

// 전역 BGM 상태 관리
let globalBgmAudio: HTMLAudioElement | null = null;
let globalBgmStarted = false;
let globalBgmPlaying = true; // 기본값을 true로 설정

export const useAudio = (options: UseAudioOptions = {}) => {
  const {
    bgmTracks = [
      '/src/assets/sounds/Edelstein_City_(NEWTRO Ver.).mp3',
      '/src/assets/sounds/Newtro_Henesys.mp3',
      '/src/assets/sounds/Newtro_Kingdom.mp3'
    ],
    enableBgm = true,
    bgmVolume = 0.5,
    clickSoundVolume = 0.7
  } = options;

  const [isBgmPlaying, setIsBgmPlaying] = useState(enableBgm);
  const clickSoundRef = useRef<HTMLAudioElement | null>(null);

  // 전역 상태와 로컬 상태 동기화
  useEffect(() => {
    if (enableBgm) {
      setIsBgmPlaying(globalBgmPlaying);
    }
  }, [enableBgm]);

  // 오디오 초기화
  useEffect(() => {
    // BGM 초기화 (enableBgm이 true일 때만, 전역 BGM이 없을 때만)
    if (enableBgm && bgmTracks.length > 0 && !globalBgmAudio) {
      const randomTrack = bgmTracks[Math.floor(Math.random() * bgmTracks.length)];
      globalBgmAudio = new Audio(randomTrack);
      globalBgmAudio.loop = true;
      globalBgmAudio.volume = bgmVolume;

      // BGM 자동 재생 시도 (사용자 상호작용 후에만 재생 가능)
      const playBgm = () => {
        if (globalBgmAudio && !globalBgmStarted && globalBgmPlaying) {
          globalBgmAudio.play().catch(console.error);
          globalBgmStarted = true;
        }
      };

      // 첫 번째 사용자 상호작용 시 BGM 재생
      const handleFirstInteraction = () => {
        playBgm();
        document.removeEventListener('click', handleFirstInteraction);
        document.removeEventListener('keydown', handleFirstInteraction);
      };

      // 이미 시작된 BGM이 있다면 이벤트 리스너 추가하지 않음
      if (!globalBgmStarted) {
        document.addEventListener('click', handleFirstInteraction);
        document.addEventListener('keydown', handleFirstInteraction);
      }

      // 정리 함수에서 이벤트 리스너 제거
      return () => {
        document.removeEventListener('click', handleFirstInteraction);
        document.removeEventListener('keydown', handleFirstInteraction);
      };
    }
  }, [enableBgm, bgmTracks, bgmVolume]);

  // 클릭 사운드 초기화
  useEffect(() => {
    clickSoundRef.current = new Audio('/src/assets/sounds/click.mp3');
    clickSoundRef.current.volume = clickSoundVolume;

    return () => {
      if (clickSoundRef.current) {
        clickSoundRef.current = null;
      }
    };
  }, [clickSoundVolume]);

  // 클릭 사운드 재생 함수
  const playClickSound = () => {
    if (clickSoundRef.current) {
      clickSoundRef.current.currentTime = 0;
      clickSoundRef.current.play().catch(console.error);
    }
  };

  // BGM 토글 함수
  const toggleBgm = () => {
    if (globalBgmAudio) {
      if (isBgmPlaying) {
        globalBgmAudio.pause();
        globalBgmPlaying = false;
        setIsBgmPlaying(false);
      } else {
        globalBgmAudio.play().catch(console.error);
        globalBgmPlaying = true;
        globalBgmStarted = true;
        setIsBgmPlaying(true);
      }
    }
  };

  return {
    isBgmPlaying,
    playClickSound,
    toggleBgm
  };
};