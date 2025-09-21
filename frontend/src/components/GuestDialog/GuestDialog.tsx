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
import { useAudio } from '@/hooks/useAudio';

interface GuestDialogProps {
  guestName: string;
  guestText: string;
  guestImage: string;
  open: boolean;
  onClose: () => void;
  onSelect: (e: React.MouseEvent<HTMLButtonElement>) => void;
}

const GuestDialog: React.FC<GuestDialogProps> = ({guestName, guestText, guestImage, onClose, onSelect }) => {
  const { playClickSound } = useAudio({ enableBgm: false });

  const handleClose = () => {
    playClickSound();
    onClose();
  };

  const handleSelect = (e: React.MouseEvent<HTMLButtonElement>) => {
    playClickSound();
    onSelect(e);
  };

  return (
    <DialogOverlay>
      <GuestBox>
        <GuestIcon 
          src={guestImage}
        ></GuestIcon>
        <DialogBox>
          <DialogHeader>{guestName}</DialogHeader>
          <DialogContent>{guestText}</DialogContent>
          <DialogActionsBox>
            <DialogActions>
              <button onClick={handleClose}>닫기</button>
            </DialogActions>
            <DialogActions>
              <button onClick={handleSelect}>선택</button>
            </DialogActions>
          </DialogActionsBox>

        </DialogBox>
      </GuestBox>
    </DialogOverlay>
  );
};

export default GuestDialog;
