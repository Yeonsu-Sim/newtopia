import React from "react";
import { MessageBox, MessageIcon } from "@/routes/game/-Game.styles";

interface MessageProps {
  text: string;
}

const Message: React.FC<MessageProps> = ({ text }) => {
  return (
    <MessageBox>
      <div>{text}</div>
      <MessageIcon />
    </MessageBox>
  );
};

export default Message;
