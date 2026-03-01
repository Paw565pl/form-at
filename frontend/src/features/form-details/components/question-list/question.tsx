import { QuestionResponseDto } from "@/core/types/question";
import { useTranslations } from "next-intl";

interface QuestionProps {
  readonly question: QuestionResponseDto;
  readonly index: number;
}

export const Question = ({ question, index }: QuestionProps) => {
  const gt = useTranslations("global");

  return (
    <div className="flex w-full gap-2">
      <p className="text-muted-foreground">{index + 1}.</p>
      <p className="min-w-0 flex-1 wrap-break-word">
        {question.content}
        <span className="text-muted-foreground">
          {question.isRequired && " *"}
        </span>
      </p>
      <p className="text-muted-foreground ml-auto text-sm text-nowrap">
        {gt(`questionTypes.${question.type}`)}
      </p>
    </div>
  );
};
