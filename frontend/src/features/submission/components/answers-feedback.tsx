import { Card } from "@/core/components/ui/card";
import { Checkbox } from "@/core/components/ui/checkbox";
import { Label } from "@/core/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/core/components/ui/radio-group";
import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";
import { QuestionResponseDto } from "@/core/types/question";
import { SubmissionAnswerRequestDto } from "@/core/types/submission";
import { useTranslations } from "next-intl";

interface AnswersFeedbackProps {
  readonly formQuestions: QuestionResponseDto[];
  readonly answers?: SubmissionAnswerRequestDto[];
}

export const AnswersFeedback = ({
  formQuestions,
  answers,
}: AnswersFeedbackProps) => {
  const gt = useTranslations("global");

  return (
    <div className="flex flex-col gap-4">
      {formQuestions.map((question, index) => {
        const matchingAnswer = answers?.find(
          (a) => a.questionId === question.id,
        );
        const openAnswer = matchingAnswer?.openAnswer;
        const selectedOptions = matchingAnswer?.chosenAnswerIds || [];

        return (
          <Card key={question.id} className="gap-2 p-4">
            <header className="flex flex-col gap-4">
              <div className="flex flex-col flex-wrap gap-1 sm:flex-row sm:items-center sm:justify-between">
                <div className="flex gap-1">
                  <span className="text-muted-foreground">{index + 1}.</span>
                  <h2 className="font-medium">
                    {question.content}{" "}
                    {question.isRequired && (
                      <span className="text-muted-foreground">*</span>
                    )}
                  </h2>
                </div>
                <span className="text-muted-foreground ml-3 text-sm">
                  {gt(`questionTypes.${question.type}`)}
                </span>
              </div>
            </header>

            {question.type === "OPEN" &&
              (openAnswer ? (
                <p className="mx-3 text-sm">{openAnswer}</p>
              ) : (
                <p className="text-muted-foreground mx-3 text-sm">
                  {gt("emptyOpenAnswer")}
                </p>
              ))}

            {question.type === "SINGLE_CHOICE" && (
              <RadioGroup disabled value={selectedOptions[0]}>
                {question.answers.map((answer) => (
                  <div key={answer.id} className="flex items-center gap-2">
                    <RadioGroupItem
                      value={answer.id}
                      id={answer.id}
                      className="cursor-default! opacity-100!"
                    />
                    <Label htmlFor={answer.id} className="font-normal">
                      {answer.content}
                    </Label>
                    {answer.isCorrect ? (
                      <ICONS.check
                        className={cn(
                          "text-green-400",
                          selectedOptions[0] !== answer.id && "opacity-30",
                        )}
                      />
                    ) : (
                      <ICONS.close
                        className={cn(
                          "text-destructive",
                          selectedOptions[0] !== answer.id && "opacity-30",
                        )}
                      />
                    )}
                  </div>
                ))}
              </RadioGroup>
            )}

            {question.type === "MULTIPLE_CHOICE" && (
              <div className="flex flex-col gap-2">
                {question.answers.map((answer) => (
                  <div key={answer.id} className="flex items-center gap-2">
                    <Checkbox
                      checked={selectedOptions.includes(answer.id)}
                      id={answer.id}
                      disabled
                      className="cursor-default! opacity-100!"
                    />
                    <Label
                      htmlFor={answer.id}
                      className="cursor-default! font-normal opacity-100!"
                    >
                      {answer.content}
                    </Label>
                    {answer.isCorrect ? (
                      <ICONS.check
                        className={cn(
                          "text-green-400",
                          !selectedOptions.includes(answer.id) && "opacity-30",
                        )}
                      />
                    ) : (
                      <ICONS.close
                        className={cn(
                          "text-destructive",
                          !selectedOptions.includes(answer.id) && "opacity-30",
                        )}
                      />
                    )}
                  </div>
                ))}
              </div>
            )}
          </Card>
        );
      })}
    </div>
  );
};
