import { QuestionType } from "@/core/types/question";
import z from "zod";

export const submissionSchema = z.object({
  answers: z.array(
    z
      .object({
        questionId: z.string(),
        chosenAnswerIds: z.array(z.string()),
        openAnswer: z.string().trim(),
        type: z.enum(QuestionType), // for validation purposes
        required: z.boolean(),
      })

      .superRefine((data, ctx) => {
        if (data.type === QuestionType.Open) {
          if (data.openAnswer.length < 10) {
            if (
              data.required ||
              (!data.required && data.openAnswer.length > 0)
            ) {
              ctx.addIssue({
                path: ["openAnswer"],
                code: "custom",
                message: "openAnswerMin",
              });
            }
          } else if (data.openAnswer.length > 1000) {
            ctx.addIssue({
              path: ["openAnswer"],
              code: "custom",
              message: "openAnswerMax",
            });
          }
        } else if (
          data.type === QuestionType.SingleChoice ||
          data.type === QuestionType.MultipleChoice
        ) {
          if (data.chosenAnswerIds.length === 0 && data.required) {
            ctx.addIssue({
              path: ["chosenAnswerIds"],
              code: "custom",
              message: "oneAnswerRequired",
            });
          }
        }
      }),
  ),
});
