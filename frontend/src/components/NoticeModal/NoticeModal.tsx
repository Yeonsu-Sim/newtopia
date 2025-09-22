import React, { useState } from 'react'
import {
    ModalOverlay,
    ModalBox,
    ModalHeader,
    CloseButton,
    NoticeListWrapper,
    NoticeItem,
    NoticeImage,
    NoticeTitle,
    NoticeContentWrapper,
    NoticeImageLarge,
    BackButton,
    NoticeContent,
    NoticeContentSection,
    NoticeEmptyWrapper,
    NoticeEmpty
} from '@/components/NoticeModal/NoticeModal.styles'

import { 
    // exampleNotices, 
    exampleNoticesEmpty 
} from '@/data/exampleResponse'

interface Notice {
  id: number;
  title: string;
  content: string;
  imgUrl: string;
}

interface NoticeModalProps {
  isOpen: boolean
  onClose: () => void
}

export const NoticeModal: React.FC<NoticeModalProps> = ({ isOpen, onClose }) => {
  const [selectedNotice, setSelectedNotice] = useState<Notice | null>(null)

  if (!isOpen) return null

  const notices: Notice[] = exampleNoticesEmpty.data

  if (!notices || notices.length === 0) {
    return (
        <ModalOverlay onClick={onClose}>
        <ModalBox onClick={e => e.stopPropagation()}>
            <CloseButton onClick={onClose}>✕</CloseButton>
            <ModalHeader>{selectedNotice ? selectedNotice.title : '공지사항'}</ModalHeader>
            <NoticeEmptyWrapper>
                <NoticeEmpty>공지사항이 없어용!</NoticeEmpty>
            </NoticeEmptyWrapper>
        </ModalBox>
        </ModalOverlay>
    )
  }

  return (
    <ModalOverlay onClick={onClose}>
    <ModalBox onClick={e => e.stopPropagation()}>
        <CloseButton onClick={onClose}>✕</CloseButton>
        <ModalHeader>{selectedNotice ? selectedNotice.title : '공지사항'}</ModalHeader>

        {!selectedNotice && (
        <NoticeListWrapper>
            {notices.map(notice => (
            <NoticeItem key={notice.id} onClick={() => setSelectedNotice(notice)}>
                <NoticeImage src={notice.imgUrl} alt={notice.title} />
                <NoticeTitle>{notice.title}</NoticeTitle>
            </NoticeItem>
            ))}
        </NoticeListWrapper>
        )}

        {selectedNotice && (
        <NoticeContentWrapper>
            <NoticeContentSection>
                <NoticeImageLarge src={selectedNotice.imgUrl} alt={selectedNotice.title} />
                <NoticeContent>{selectedNotice.content}</NoticeContent>
            </NoticeContentSection>
            <BackButton onClick={() => setSelectedNotice(null)}>뒤로가기</BackButton>
        </NoticeContentWrapper>
        )}
    </ModalBox>
    </ModalOverlay>
  )
}
