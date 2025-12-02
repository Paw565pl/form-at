import { QuestionResponseDto, QuestionType } from "@/core/types/question";
import { useTranslations } from "next-intl";

interface QuestionProps {
  readonly question: QuestionResponseDto;
  readonly index: number;
}

export const Question = ({ question, index }: QuestionProps) => {
  const t = useTranslations("formDetailsPage.questionList");

  const questionTypes: Record<QuestionType, string> = {
    SINGLE_CHOICE: t("questionTypes.single"),
    MULTIPLE_CHOICE: t("questionTypes.multiple"),
    OPEN: t("questionTypes.open"),
  } as const;

  return (
    <div className="flex w-full items-center gap-2">
      <p className="text-muted-foreground">{index + 1}.</p>
      <p>
        {question.content}
        <span className="text-muted-foreground">
          {question.isRequired && " *"}
        </span>
      </p>
      <p className="text-muted-foreground ml-auto text-sm">
        {questionTypes[question.type]}
      </p>
    </div>
  );
};
