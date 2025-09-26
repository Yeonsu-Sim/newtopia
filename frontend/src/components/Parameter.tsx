import { useEffect, useRef, useState } from 'react'
import {
  ParameterWrapper,
  ProgressBar,
  ProgressFill,
  ProgressBox,
  ProgressValue,
  ParameterIcon,
  ParameterTooltip,
  ParameterDiff,
} from '@/routes/game/-Game.styles'
import { type ParameterChangeLevel } from '@/services/game/gameService'

interface ParameterProps {
  type: 'eco' | 'env' | 'opi' | 'mil'
  value: number
  x: number
  y: number
  highlightLevel?: ParameterChangeLevel
}

const Parameter: React.FC<ParameterProps> = ({ type, value, x, y, highlightLevel }) => {
  let level = 1
  if (value >= 75) level = 4
  else if (value >= 50) level = 3
  else if (value >= 25) level = 2

  const prevValue = useRef(value)
  const [diff, setDiff] = useState<number | null>(null)

  useEffect(() => {
    if (prevValue.current !== value) {
      setDiff(value - prevValue.current)
      prevValue.current = value

      const timer = setTimeout(() => setDiff(null), 1500)
      return () => clearTimeout(timer)
    }
  }, [value])

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

  // 하이라이트 애니메이션 클래스 결정
  const getHighlightClass = () => {
    if (!highlightLevel || highlightLevel === 'none') return ''
    return `highlight-${highlightLevel}`
  }

  const getFeedback = (type: string, value: number) => {
    if (type === 'eco') {
      if (value < 25) return '경제 붕괴 직전!'
      if (value < 50) return '경기 침체 우려'
      if (value < 75) return '안정적 성장'
      return '호황기 진입!'
    }
    if (type === 'mil') {
      if (value < 25) return '국가 방어 불가'
      if (value < 50) return '방어력 취약'
      if (value < 75) return '전투력 양호'
      return '압도적 군사력'
    }
    if (type === 'opi') {
      if (value < 25) return '민중의 분노 폭발'
      if (value < 50) return '불만 확산 중'
      if (value < 75) return '대체로 만족'
      return '절대적 지지'
    }
    if (type === 'env') {
      if (value < 25) return '환경 재앙 수준'
      if (value < 50) return '오염 심각'
      if (value < 75) return '환경 안정적'
      return '친환경 모범국'
    }
    return ''
  }

  return (
    <ParameterWrapper x={x} y={y}>
      <ParameterIcon
        $type={type}
        $level={level}
        className={getHighlightClass()}
        src={`/parameters/${type}_${level}.png`}
        alt={`${type} parameter icon`}
      />
      <ParameterTooltip className="parameter-tooltip">
        <strong>{getParameterLabel(type)}</strong>
        <br />
        {getFeedback(type, value)}
      </ParameterTooltip>
      <ProgressBox>
        <ProgressBar>
          <ProgressFill value={value} max={100} />
        </ProgressBar>
        <ProgressValue>
          {value}
          {diff !== null && diff !== 0 && (
            <ParameterDiff $diff={diff}>
              {diff > 0 ? `+${diff}` : diff}
            </ParameterDiff>
          )}
        </ProgressValue>
      </ProgressBox>
    </ParameterWrapper>
  )
}

export default Parameter
