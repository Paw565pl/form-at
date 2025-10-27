import { Checkbox } from "@/core/components/ui/checkbox";
import { Label } from "@/core/components/ui/label";

interface QuestionProps {
  name: string;
  imageKey?: string;
  answers: { content: string }[];
}

export const Question = (question: QuestionProps) => {
  return (
    <div className="flex gap-2 rounded-lg border-2 border-black p-2">
      {question.imageKey && <div className="h-32 w-32 bg-amber-600"></div>}
      <div className="flex flex-col gap-2">
        <h1 className="w-full text-center text-xl">{question.name}</h1>
        {question.answers.map((answer, index) => (
          <div key={index} className="flex items-center gap-3">
            <Checkbox disabled />
            <Label>{answer.content}</Label>
          </div>
        ))}
      </div>
    </div>
  );
};
