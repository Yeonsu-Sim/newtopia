import React from "react";

import {
  DialogOverlay,
  DialogBox,
  CloseButton,
  DialogTitle,
  NewsTitle,
} from '@/components/FeedbackDialog/FeedbackDialog.styles'

interface Article {
  title: string;
  url: string;
};

interface FeedbackDialogProps {
  open: boolean;
  article: Article;
  onClose: () => void;
}

const FeedbackDialog: React.FC<FeedbackDialogProps> = ({ article, onClose }) => {
  return (
    <DialogOverlay>
      <DialogBox>
        <DialogTitle>뉴스 속보</DialogTitle>
        <div>
          <NewsTitle>제목: {article.title}</NewsTitle>
        </div>
        <CloseButton onClick={onClose}>확인</CloseButton>
      </DialogBox>
    </DialogOverlay>
  );
};

export default FeedbackDialog;
