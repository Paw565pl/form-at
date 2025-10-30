"use client";
import { Button } from "@/core/components/ui/button";
import { Question } from "@/features/public-form-view/components/question";
import { QuestionResponseDto } from "@/features/public-form-view/types/question-response-dto";
import { ChevronDown } from "lucide-react";
import { useTranslations } from "next-intl";
import { useState } from "react";

interface QuestionListProps {
  questions: QuestionResponseDto[];
}

export const QuestionList = ({ questions }: QuestionListProps) => {
  const t = useTranslations("PublicFormView.QuestionList");
  const [showQuestions, setShowQuestions] = useState(true);

  return (
    <div className="my-2 flex flex-col gap-2">
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
