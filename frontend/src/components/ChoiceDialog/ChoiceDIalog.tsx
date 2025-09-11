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

interface ChoiceOption {
  code: string;
  label: string;
}

interface CountryStats {
  eco: number;
  mil: number;
  opi: number;
  env: number;
}

interface ChoiceDialogProps {
  guestText: string;
  choices: ChoiceOption[];
  currentStats: CountryStats;
  open: boolean;
  onBack: () => void;
  onSelect: (choiceCode: string) => void;
}

const ChoiceDialog: React.FC<ChoiceDialogProps> = ({ guestText, choices, currentStats, onBack, onSelect }) => {
  return (
    <DialogOverlay>
      <ParameterChangeBox>
        <ParameterChange type="eco" value={currentStats.eco} />
        <ParameterChange type="env" value={currentStats.env} />
        <ParameterChange type="cit" value={currentStats.opi} />
        <ParameterChange type="def" value={currentStats.mil} />
      </ParameterChangeBox>
        
      <DialogBox>
        <h2>{guestText}</h2>
        <ChoiceCards>
          {choices[0] && <CardA onClick={() => onSelect(choices[0].code)}>{choices[0].label}</CardA>}
          {choices[1] && <CardB onClick={() => onSelect(choices[1].code)}>{choices[1].label}</CardB>}
        </ChoiceCards>
        <CloseButton onClick={onBack}>뒤로가기</CloseButton>
      </DialogBox>
    </DialogOverlay>
  );
};

export default ChoiceDialog;
