import { useState, useEffect } from "react";
import styled, { keyframes } from "styled-components";

const slideIn = keyframes`
  from { transform: translateX(-120%); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
`;

type Toast = { 
  id: string; 
  message: string; 
  like: number; 
  icon: string; 
};

interface FeedbackToastContainerProps {
  messages: string[];
  intervalTime?: number; // 토스트 쌓이는 간격(ms)
  displayTime?: number;  // 토스트 유지 시간(ms)
}

export default function FeedbackToastContainer({
  messages,
  intervalTime = 1000,
  displayTime = 8000,
}: FeedbackToastContainerProps) {
  const [toasts, setToasts] = useState<Toast[]>([]);

  useEffect(() => {
    if (!messages || messages.length === 0) return;

    let isCancelled = false;
    setToasts([]); // 초기화

    messages.forEach((msg, idx) => {
      if (!msg) return;

      const showTimer = setTimeout(() => {
        if (isCancelled) return;
        const id = `${Date.now()}-${idx}`;
        const like = Math.floor(Math.random() * 100) + 1; // 1~100 랜덤
        const icon = `/icons/${Math.floor(Math.random() * 4) + 1}.png`; // 1~4 랜덤

        setToasts(prev => [...prev, { id, message: msg, like, icon }]);

        const hideTimer = setTimeout(() => {
          if (isCancelled) return;
          setToasts(prev => prev.filter(t => t.id !== id));
        }, displayTime);

        return () => clearTimeout(hideTimer);
      }, idx * intervalTime);

      return () => clearTimeout(showTimer);
    });

    return () => {
      isCancelled = true;
      setToasts([]);
    };
  }, [messages, intervalTime, displayTime]);

  return (
    <ToastContainer>
      {toasts.map(t => (
        <ToastItem key={t.id}>
          <NpcIcon src={t.icon} alt="npc" />
          <TextWrapper>
            <NpcMessage>{t.message}</NpcMessage>
            <Like>❤️ {t.like}</Like>
          </TextWrapper>
        </ToastItem>
      ))}
    </ToastContainer>
  );
}

const ToastContainer = styled.div`
  position: fixed;
  bottom: 20px;
  left: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

const ToastItem = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(0,0,0,0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  pointer-events: auto;
  animation: ${slideIn} 0.4s ease forwards;
`;

const NpcIcon = styled.img`
  border-radius: 100%;
  width: 50px;
  height: auto;
`;

const TextWrapper = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
`;

const NpcMessage = styled.div`
  text-align: start;
`;

const Like = styled.div`
  text-align: end;
  font-size: 0.8rem;
  margin-top: 4px;
  opacity: 0.9;
`;
