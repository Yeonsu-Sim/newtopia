import React, { useEffect, useState } from 'react'
import styled, { keyframes } from 'styled-components'

const slideIn = keyframes`
  from { transform: translateX(-120%); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
`

const slideOut = keyframes`
  from { transform: translateX(0); opacity: 1; }
  to { transform: translateX(-120%); opacity: 0; }
`

const ToastWrapper = styled.div<{ $leaving?: boolean }>`
  background: rgba(0, 0, 0, 0.85);
  color: #fff;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  animation: ${({ $leaving }) => ($leaving ? slideOut : slideIn)} 0.4s ease
    forwards;
`

type Props = {
  message: string
  duration?: number
  onClose: () => void
}

const FeedbackToast: React.FC<Props> = ({
  message,
  duration = 2000,
  onClose,
}) => {
  const [leaving, setLeaving] = useState(false)

  useEffect(() => {
    const leaveTimer = setTimeout(() => setLeaving(true), duration - 400)
    const removeTimer = setTimeout(onClose, duration)
    return () => {
      clearTimeout(leaveTimer)
      clearTimeout(removeTimer)
    }
  }, [duration, onClose])

  return <ToastWrapper $leaving={leaving}>{message}</ToastWrapper>
}

export default FeedbackToast
