import{
  DialogOverlay,
  GuestBox,
  GuestIcon,
  DialogBox,
  DialogActionsBox,
  DialogHeader,
  DialogContent,
  DialogActions
} from '@/components/GuestDialog/GuestDialog.styles';

interface GuestDialogProps {
  guestName: string;
  guestText: string;
  open: boolean;
  onClose: () => void;
  onSelect: () => void;
}

const GuestDialog: React.FC<GuestDialogProps> = ({guestName, guestText, onClose, onSelect }) => {
  return (
    <DialogOverlay>
      <GuestBox>
        <GuestIcon></GuestIcon>
        <DialogBox>
          <DialogHeader>{guestName}</DialogHeader>
          <DialogContent>{guestText}</DialogContent>
          <DialogActionsBox>
            <DialogActions>
              <button onClick={onClose}>닫기</button>
            </DialogActions>
            <DialogActions>
              <button onClick={onSelect}>선택</button>
            </DialogActions>
          </DialogActionsBox>

        </DialogBox>
      </GuestBox>
    </DialogOverlay>
  );
};

export default GuestDialog;
