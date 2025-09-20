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

export const prepareChartData = (series: GraphSeries[]): ChartPoint[] => {
  const totalTurns = series[0].points.length;

  const data: ChartPoint[] = [];

  for (let i = 0; i < totalTurns; i++) {
    const point: ChartPoint = { turn: i };
    series.forEach((s) => {
      point[s.metric] = s.points[i].value;
    });
    data.push(point);
  }

  return data;
};
