import {
  DialogOverlay,
  GuestBox,
  GuestIcon,
  DialogBox,
  DialogActionsBox,
  DialogHeader,
  DialogContent,
  DialogActions,
} from '@/components/GuestDialog/GuestDialog.styles'
import { useAudio } from '@/hooks/useAudio'
import { useTypewriter } from '@/hooks/useTypewriter'

interface GuestDialogProps {
  guestName: string
  guestText: string
  guestImage: string
  open: boolean
  onClose: () => void
  onSelect: (e: React.MouseEvent<HTMLButtonElement>) => void
  variant?: 'default' | 'onboarding'
  closeButtonText?: string
  selectButtonText?: string
  enableTypewriter?: boolean
}

const GuestDialog: React.FC<GuestDialogProps> = ({
  guestName,
  guestText,
  guestImage,
  onClose,
  onSelect,
  variant = 'default',
  closeButtonText = '닫기',
  selectButtonText = '선택',
  enableTypewriter = true,
}) => {
  const { playClickSound } = useAudio({ enableBgm: false })
  const { displayText } = useTypewriter({
    text: guestText,
    speed: 50,
    startDelay: 300,
  })

  const finalText = enableTypewriter ? displayText : guestText

  const handleClose = () => {
    playClickSound()
    onClose()
  }

  const handleSelect = (e: React.MouseEvent<HTMLButtonElement>) => {
    playClickSound()
    onSelect(e)
  }

  return (
    <DialogOverlay $variant={variant}>
      <GuestBox>
        <GuestIcon src={guestImage}></GuestIcon>
        <DialogBox $variant={variant}>
          <DialogHeader $variant={variant}>{guestName}</DialogHeader>
          <DialogContent $variant={variant}>{finalText}</DialogContent>
          <DialogActionsBox>
            <DialogActions $variant={variant}>
              <button onClick={handleClose}>{closeButtonText}</button>
            </DialogActions>
            <DialogActions $variant={variant}>
              <button onClick={handleSelect}>{selectButtonText}</button>
            </DialogActions>
          </DialogActionsBox>
        </DialogBox>
      </GuestBox>
    </DialogOverlay>
  )
}

export default GuestDialog
