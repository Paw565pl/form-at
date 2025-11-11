"use client";
import { Button } from "@/core/components/ui/button";
import { QuestionResponseDto } from "@/core/types/question";
import { Question } from "@/features/form-details/components/question/question";
import { ChevronDown } from "lucide-react";
import { useTranslations } from "next-intl";
import { useState } from "react";

interface QuestionListProps {
  questions: QuestionResponseDto[];
}

export const QuestionList = ({ questions }: QuestionListProps) => {
  const t = useTranslations("publicFormView.questionList");
  const [showQuestions, setShowQuestions] = useState(true);

  return (
    <div className="flex w-full flex-col gap-2 py-2">
      <div className="flex gap-2">
        <h1 className="text-xl">{t("showQuestions")}</h1>
        <Button
          size={"icon-sm"}
          onClick={() => setShowQuestions((prev) => !prev)}
        >
          <ChevronDown />
        </Button>
      </div>
      {showQuestions &&
        questions.map((question, idx) => <Question key={idx} {...question} />)}
    </div>
  );
};
