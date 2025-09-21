import React from 'react'
import { useAudio } from '@/hooks/useAudio'

import {
  DialogOverlay,
  DialogBox,
  CloseButton,
  NewsTitle,
  ContentContainer,
  ContentArea,
  ContentText,
} from '@/components/FeedbackDialog/FeedbackDialog.styles'

interface Article {
  title: string
  url: string
  content?: string
}

interface FeedbackDialogProps {
  open: boolean
  article: Article
  onClose: () => void
}

const FeedbackDialog: React.FC<FeedbackDialogProps> = ({
  article,
  onClose,
}) => {
  const { playClickSound } = useAudio({ enableBgm: false })

  const handleClose = () => {
    playClickSound()
    onClose()
  }

  const formatContent = (content: string): string => {
    return (
      content
        // 마침표 뒤에 줄바꿈 추가 (단, 숫자 뒤 마침표는 제외)
        .replace(/\. (?![0-9])/g, '.\n\n')
        // 큰따옴표 뒤에 줄바꿈 추가
        .replace(/" /g, '"\n\n')
        // 연속된 줄바꿈 정리
        .replace(/\n{3,}/g, '\n\n')
        .trim()
    )
  }
  return (
    <DialogOverlay>
      <DialogBox>
        {/* <DialogTitle>뉴스 속보</DialogTitle> */}
        <ContentContainer>

          {article.content && (
            <ContentArea>
                        <NewsTitle>
            <a href={article.url} target="_blank" rel="noopener noreferrer">
              {article.title}
            </a>
          </NewsTitle>
              <ContentText>{formatContent(article.content)}</ContentText>
            </ContentArea>
          )}
        </ContentContainer>
        <CloseButton onClick={handleClose}>확인</CloseButton>
      </DialogBox>
    </DialogOverlay>
  )
}

export default FeedbackDialog
