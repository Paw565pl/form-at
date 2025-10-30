"use client";
import { Button } from "@/core/components/ui/button";
import { ChevronDown } from "lucide-react";
import { useState } from "react";
import { QuestionResponseDto } from "../types/question-response-dto";
import { Question } from "./question";

interface QuestionListProps {
  questions: QuestionResponseDto[];
}

export const QuestionList = ({ questions }: QuestionListProps) => {
  const [showQuestions, setShowQuestions] = useState(true);

  return (
    <div className="my-2 flex flex-col gap-2">
      <div className="flex gap-2">
        <h1 className="text-xl">Pokaż pytania</h1>
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
