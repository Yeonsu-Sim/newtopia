import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { prepareChartData } from '@/utils/prepareChartData';

interface ChartPoint {
  turn: number;
  [key: string]: number | string;
}

interface GraphPoint {
  turnNumber: number;
  value: number;
}

interface GraphSeries {
  metric: string;
  points: GraphPoint[];
}

interface ReportLineChartProps {
  width?: number;
  height?: number;
  series: GraphSeries[];
  onClickTurn?: (turn: number) => void;
}

const metricColors: { [key: string]: string } = {
  economy: "#5F81FF",
  defense: "#FF6B6B",
  publicSentiment: "#FFD93D",
  environment: "#00C49F",
};

const metricLabels: { [key: string]: string } = {
  economy: "경제",
  environment: "환경",
  publicSentiment: "민심",
  defense: "국방",
};

const ReportLineChart: React.FC<ReportLineChartProps> = ({ width = 600, height = 350, series, onClickTurn }) => {
  const data: ChartPoint[] = prepareChartData(series);

  const metrics = Object.keys(data[0]).filter((k) => k !== 'turn');
  const chartWidth = Math.max(width, data.length * 100);

  return (
    <LineChart
      width={chartWidth}
      height={height}
      data={data}
      margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
    >
      <CartesianGrid stroke="#eee" strokeDasharray="5 5" />
      <XAxis dataKey="turn" label={{ value: "턴", position: "insideBottomRight", offset: -5 }} />
      <YAxis />
      <Tooltip />
      <Legend />
      {metrics.map((metric) => (
        <Line
          key={metric}
          type="monotone"
          dataKey={metric}
          stroke={metricColors[metric] || "#000"}
          name={metricLabels[metric] || metric}
          dot={(dotProps: any) => {
            const { cx, cy, payload } = dotProps;
            if (!payload) return <></>;

            return (
              <circle
                cx={cx}
                cy={cy}
                r={10}
                fill={metricColors[metric] || "#000"}
                stroke="#fff"
                strokeWidth={1}
                cursor="pointer"
                style={{ pointerEvents: "all" }}
                onClick={() => {
                  console.log("클릭 턴:", payload.turn); // 콘솔 확인
                  if (onClickTurn) onClickTurn(payload.turn);
                }}
              />
            );
          }}
        />
      ))}
    </LineChart>
  );
};

export default ReportLineChart;
