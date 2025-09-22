import styled from 'styled-components'
import { CircularProgressbar, buildStyles } from 'react-circular-progressbar'
import ChangingProgressProvider from '@/components/CircularProgress/ChangingProgressProvider'

interface CircularProgressProps {
  size?: string
  value?: number
  pathColor?: string
  trailColor?: string
  textColor?: string
  label?: string
}
export const CircularProgressChart = ({
  size = '100px',
  value = 50,
  pathColor = '#5F81FF',
  trailColor = '#DFE8FF',
  textColor = '#2B2D36',
  label,
}: CircularProgressProps) => {
  return (
    <ProgressbarWrapper $size={size}>
      <ProgressbarContainer $size={size}>
        <ChangingProgressProvider values={[0, value]}>
          {(percentage) => (
            <CircularProgressbar
              value={percentage}
              // text={`${percentage}%`}
              className="progressbar"
              strokeWidth={10}
              styles={buildStyles({
                pathColor,
                trailColor,
                textColor,
              })}
            />
          )}
        </ChangingProgressProvider>
      </ProgressbarContainer>
      <Percent>{value}%</Percent>
      {label && <Label>{label}</Label>}
    </ProgressbarWrapper>
  )
}

interface ContainerProps {
  $size: string
}

const ProgressbarWrapper = styled.div<ContainerProps>`
  display: flex;
  flex-direction: column;
  align-items: center;
`

const ProgressbarContainer = styled.div<ContainerProps>`
  width: ${({ $size }) => $size};
  height: ${({ $size }) => $size};
  font-weight: 600;
`

const Percent = styled.div`
  margin-top: 8px;
  font-size: 15px;
  color: #333;
  font-family: 'PFStardustExtraBold', 'Noto Sans KR', sans-serif;
`

const Label = styled.div`
  font-size: 14px;
  color: #333;
  font-family: 'PFStardustExtraBold', 'Noto Sans KR', sans-serif;
`
