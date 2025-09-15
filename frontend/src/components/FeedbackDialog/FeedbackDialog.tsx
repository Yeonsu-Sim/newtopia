import React from "react";

import {
  DialogOverlay,
  DialogBox,
  CloseButton,
  DialogTitle,
  NewsTitle,
  // CommentBox,
  // Comment
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
        <iframe
          src={article.url}
          width="100%"
          height="400px"
          style={{ border: "none", borderRadius: "12px", marginTop: "1rem" }}
          title={article.title}
        />
        <CloseButton onClick={onClose}>확인</CloseButton>
      </DialogBox>
      {/* <CommentBox>
        <Comment>와 정말 멋지군요.</Comment>
        <Comment>구린것 같아요.</Comment>
        <Comment>별론데;</Comment>
        <Comment>이걸 이렇게?</Comment>
      </CommentBox> */}
    </DialogOverlay>
  );
};

export default FeedbackDialog;
