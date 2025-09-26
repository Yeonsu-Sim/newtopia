import {
  ParameterChangeWrapper,
  ProgressBar,
  ProgressFill,
  ProgressBox,
  ProgressValue,
  ParameterEmoji,
} from '@/routes/game/-Game.styles'
import { type ParameterChangeLevel } from '@/services/game/gameService'

interface ParameterProps {
  type: 'eco' | 'env' | 'opi' | 'mil'
  value: number
  highlightLevel?: ParameterChangeLevel
}

const ParameterChange: React.FC<ParameterProps> = ({ type, value, highlightLevel }) => {
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

  // 하이라이트 애니메이션 클래스 결정
  const getHighlightClass = () => {
    if (!highlightLevel || highlightLevel === 'none') return ''
    return `highlight-${highlightLevel}`
  }

  return (
    <ParameterChangeWrapper>
      <ParameterEmoji className={getHighlightClass()}>{getEmoji(type)}</ParameterEmoji>
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
