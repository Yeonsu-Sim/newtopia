import {
  ParameterChangeWrapper,
  ProgressBar,
  ProgressFill,
  ProgressBox,
  ProgressValue,
  ParameterEmoji,
} from '@/routes/game/-Game.styles'

interface ParameterProps {
  type: 'eco' | 'env' | 'opi' | 'mil'
  value: number
}

const ParameterChange: React.FC<ParameterProps> = ({ type, value }) => {
  const getEmoji = (t: ParameterProps['type']) => {
    switch (t) {
      case 'eco':
        return '💰'
      case 'env':
        return '🌱'
      case 'opi':
        return '👥'
      case 'mil':
        return '🛡️'
      default:
        return '❓'
    }
  }

  return (
    <ParameterChangeWrapper>
      <ParameterEmoji>{getEmoji(type)}</ParameterEmoji>
      <ProgressBox>
        <ProgressBar>
          <ProgressFill value={value} max={100} />
        </ProgressBar>
        <ProgressValue>{value}</ProgressValue>
      </ProgressBox>
    </ParameterChangeWrapper>
  )
}

export default ParameterChange
