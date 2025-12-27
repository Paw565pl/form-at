interface StatisticCardProps {
  readonly value: number;
  readonly label: string;
}

export const StatisticsCard = ({ value, label }: StatisticCardProps) => {
  return (
    <div className="flex flex-1 flex-col items-center rounded-md border p-4">
      <span className="text-xl font-bold md:text-2xl">{value}</span>
      <span className="text-muted-foreground text-center text-xs md:text-sm">
        {label}
      </span>
    </div>
  );
};
