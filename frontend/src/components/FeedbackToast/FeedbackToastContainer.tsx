import { useState, useEffect } from "react";
import styled, { keyframes } from "styled-components";

const slideIn = keyframes`
  from { transform: translateX(-120%); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
`;


type Toast = { id: string; message: string };

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
        setToasts(prev => [...prev, { id, message: msg }]);

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
    <ToastWrapper>
      {toasts.map(t => (
        <ToastItem key={t.id}>{t.message}</ToastItem>
      ))}
    </ToastWrapper>
  );
}

const ToastWrapper = styled.div`
  position: absolute; // BackgroundWrapper 기준
  bottom: 20px;
  left: 20px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  pointer-events: none;
  z-index: 999;
`;

const ToastItem = styled.div`
  background: rgba(0,0,0,0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  pointer-events: auto;
  animation: ${slideIn} 0.4s ease forwards;
`;
