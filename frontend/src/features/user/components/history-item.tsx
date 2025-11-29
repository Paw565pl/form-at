import { Badge } from "@/core/components/ui/badge";

const historyItem = {
  content: "User John Doe has made submission to form: ",
  formName: "Quiz o kotach",
  date: "12.03.2024",
};

interface HistoryItemProps {
  content: string;
  formName: string;
  date: string;
  badgeVariant?: "default" | "secondary" | "destructive" | "outline";
}

export const HistoryItem = ({
  content,
  formName,
  date,
  badgeVariant,
}: HistoryItemProps) => {
  return (
    <section className="bg-card flex flex-col items-start justify-between gap-1 rounded-md p-2 shadow-sm md:flex-row md:items-center">
      <div className="flex flex-col gap-2 md:flex-row">
        {content}
        <Badge variant={badgeVariant} className="text-white">
          {formName}
        </Badge>
      </div>
      <Badge className="flex-none self-end md:self-start" variant={"outline"}>
        {date}
      </Badge>
    </section>
  );
};
