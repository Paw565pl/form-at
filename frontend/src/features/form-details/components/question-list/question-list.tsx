"use client";
import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { ICONS } from "@/core/config/icons";
import { FormDetailResponseDto } from "@/core/types/form";
import { Question } from "@/features/form-details/components/question-list/question";
import { useTranslations } from "next-intl";
import { useState } from "react";

interface QuestionListProps {
  readonly form: FormDetailResponseDto;
}

export const QuestionList = ({ form }: QuestionListProps) => {
  const t = useTranslations("formDetailsPage.questionList");
  const [showQuestions, setShowQuestions] = useState(true);

  if (!form.allowsQuestionsPreview)
    return (
      <p className="text-muted-foreground p-4 text-sm">
        {t("noPreviewAllowed")}
      </p>
    );

  return (
    <section className="mt-2 flex w-full flex-col gap-2">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => setShowQuestions((prev) => !prev)}
        className="w-fit"
      >
        {showQuestions ? t("hideQuestions") : t("showQuestions")}
        <ICONS.expandDown
          className={`transition-transform ${showQuestions ? "rotate-180" : ""}`}
        />
      </Button>

      {showQuestions && (
        <Card className="gap-1 p-4">
          {form.questions.map((question, index) => (
            <Question key={question.id} question={question} index={index} />
          ))}
        </Card>
      )}
    </section>
  );
};
