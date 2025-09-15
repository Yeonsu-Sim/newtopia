import React from "react";

import {
  DialogOverlay,
  DialogBox,
  CloseButton,
  CommentBox,
  Comment
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
        <h2>뉴스 속보</h2>
        <p>선택과 관련된 뉴스입니다.</p>
        <div>
          <strong>제목:</strong> {article.title}<br />
          <strong>링크:</strong>{" "}
          <a href={article.url} target="_blank" rel="noopener noreferrer">
            {article.url}
          </a>
        </div>
        <CloseButton onClick={onClose}>확인</CloseButton>
      </DialogBox>
      <CommentBox>
        <Comment>와 정말 멋지군요.</Comment>
        <Comment>구린것 같아요.</Comment>
        <Comment>별론데;</Comment>
        <Comment>이걸 이렇게?</Comment>
      </CommentBox>
    </DialogOverlay>
  );
};

export default FeedbackDialog;
