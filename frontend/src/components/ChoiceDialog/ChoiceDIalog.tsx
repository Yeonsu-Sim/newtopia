import React from "react";

import {
    ParameterChangeBox
} from '@/routes/game/-Game.styles';

import {
    DialogOverlay,
    DialogBox,
    ChoiceCards,
    CardA,
    CardB,
    CloseButton
} from '@/components/ChoiceDialog/ChoiceDialog.styles';

import ParameterChange from '@/components/ParameterChange';

interface ChoiceDialogProps {
  guestText: string;
  open: boolean;
  onBack: () => void;
  onSelect: () => void;
}

const ChoiceDialog: React.FC<ChoiceDialogProps> = ({ guestText, onBack, onSelect }) => {
  return (
    <DialogOverlay>
      <ParameterChangeBox>
        <ParameterChange type="eco" value={70} />
        <ParameterChange type="env" value={70} />
        <ParameterChange type="cit" value={70} />
        <ParameterChange type="def" value={70} />
      </ParameterChangeBox>
        
      <DialogBox>
        <h2>{guestText}</h2>
        <ChoiceCards>
          <CardA onClick={onSelect}>선택지 1</CardA>
          <CardB onClick={onSelect}>선택지 2</CardB>
        </ChoiceCards>
        <CloseButton onClick={onBack}>뒤로가기</CloseButton>
      </DialogBox>
    </DialogOverlay>
  );
};

export default ChoiceDialog;
