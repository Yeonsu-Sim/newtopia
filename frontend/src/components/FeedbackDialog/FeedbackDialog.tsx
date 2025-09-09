import React from "react";

import {
  DialogOverlay,
  DialogBox,
  CloseButton,
  CommentBox,
  Comment
} from '@/components/FeedbackDialog/FeedbackDialog.styles'

interface FeedbackDialogProps {
  open: boolean;
  onClose: () => void;
}

const FeedbackDialog: React.FC<FeedbackDialogProps> = ({ onClose }) => {
  return (
    <DialogOverlay>
      <DialogBox>
        <h2>뉴스 속보</h2>
        <p>선택에 따른 뉴스 내용입니다.</p>
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
