import{
  ParameterWrapper,
  ProgressBar,
  ProgressFill,
  ProgressBox,
  ProgressValue,
  ParameterEmoji
} from '@/routes/game/-Game.styles'

interface ParameterProps {
  type: "eco" | "env" | "cit" | "def";
  value: number;
}

const ParameterChange: React.FC<ParameterProps> = ({ type, value }) => {
  const getEmoji = (t: ParameterProps["type"]) => {
    switch (t) {
      case "eco":
        return "💰";
      case "env":
        return "🌱";
      case "cit":
        return "👥";
      case "def":
        return "🛡️";
      default:
        return "❓";
    }
  };

  return (
    <ParameterWrapper>
      <ParameterEmoji>{getEmoji(type)}</ParameterEmoji>
      <ProgressBox>
        <ProgressBar>
          <ProgressFill value={value} max={100} />
        </ProgressBar>
        <ProgressValue>{value}</ProgressValue>
      </ProgressBox>
    </ParameterWrapper>
  );
};

export default ParameterChange;