import{
  ParameterWrapper,
  ProgressBar,
  ProgressFill,
  ProgressBox,
  ProgressValue,
  ParameterIcon
} from '@/routes/game/-Game.styles'

interface ParameterProps {
  type: "eco" | "env" | "opi" | "mil";
  value: number;
}

const Parameter: React.FC<ParameterProps> = ({ type, value }) => {
  let level = 1;
  if (value >= 75) level = 4;
  else if (value >= 50) level = 3;
  else if (value >= 25) level = 2;

  return (
    <ParameterWrapper>
      <ParameterIcon
        type={type}
        level={level}
        src={`src/assets/parameters/${type}_${level}.png`}
      />
      <ProgressBox>
        <ProgressBar>
          <ProgressFill value={value} max={100} />
        </ProgressBar>
        <ProgressValue>{value}</ProgressValue>
      </ProgressBox>
    </ParameterWrapper>
  );
};

export default Parameter;