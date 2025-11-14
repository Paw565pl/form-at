import { Label } from "@/core/components/ui/label";
import { QuestionResponseDto } from "@/core/types/question";
import { placeholder_image_url } from "@/features/form-list/example-forms";
import Image from "next/image";

export const Question = (question: QuestionResponseDto) => {
  return (
    <div className="border-primary/60 flex gap-2 rounded-lg border p-2 shadow-sm">
      {/* placeholder for when the library is ready */}
      {question.imageKey && (
        <Image
          src={placeholder_image_url}
          alt="Question image"
          width={128}
          height={128}
        />
      )}

      <div className="flex flex-col gap-2">
        <h1 className="w-full text-xl">{question.content}</h1>
        {question.answers?.map((answer, index) => (
          <div key={index} className="flex items-center gap-3">
            <Label className="text-secondary">{answer.content}</Label>
          </div>
        ))}
      </div>
    </div>
  );
};
