import {
  ParameterWrapper,
  ProgressBar,
  ProgressFill,
  ProgressBox,
  ProgressValue,
  ParameterIcon,
  ParameterTooltip,
} from '@/routes/game/-Game.styles'

interface ParameterProps {
  type: 'eco' | 'env' | 'opi' | 'mil'
  value: number
  x: number
  y: number
}

const Parameter: React.FC<ParameterProps> = ({ type, value, x, y }) => {
  let level = 1
  if (value >= 75) level = 4
  else if (value >= 50) level = 3
  else if (value >= 25) level = 2

  const getParameterLabel = (type: string) => {
    switch (type) {
      case 'eco':
        return '경제'
      case 'env':
        return '환경'
      case 'opi':
        return '민심'
      case 'mil':
        return '국방'
      default:
        return ''
    }
  }

  return (
    <ParameterWrapper x={x} y={y}>
      <ParameterIcon
        $type={type}
        $level={level}
        src={`/parameters/${type}_${level}.png`}
        alt={`${type} parameter icon`}
      />
      <ParameterTooltip className="parameter-tooltip">
        {getParameterLabel(type)}
      </ParameterTooltip>
      <ProgressBox>
        <ProgressBar>
          <ProgressFill value={value} max={100} />
        </ProgressBar>
        <ProgressValue>{value}</ProgressValue>
      </ProgressBox>
    </ParameterWrapper>
  )
}

export default Parameter
