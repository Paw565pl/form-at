import { Checkbox } from "@/core/components/ui/checkbox";
import { Label } from "@/core/components/ui/label";
import { QuestionResponseDto } from "@/core/types/question";

export const Question = (question: QuestionResponseDto) => {
  return (
    <div className="flex gap-2 rounded-lg border-2 p-2">
      {/* placeholder for when the library is ready */}
      {question.imageKey && <div className="bg-chart-5 h-32 w-32"></div>}

      <div className="flex flex-col gap-2">
        <h1 className="w-full text-center text-xl">{question.content}</h1>
        {question.answers?.map((answer, index) => (
          <div key={index} className="flex items-center gap-3">
            <Checkbox disabled />
            <Label>{answer.content}</Label>
          </div>
        ))}
      </div>
    </div>
  );
};
