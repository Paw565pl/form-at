import en from "@/../messages/en.json";
import {
  FormEstimatedDuration,
  FormShuffleVariant,
  FormStatus,
  Language,
} from "@/core/types/form";
import { QuestionType } from "@/core/types/question";
import z from "zod";
export type ErrorKey = keyof typeof en.formCreatePage.errors;

const maxFileSizeInBytes = 10 * 1024 * 1024;
const validImageTypes = [
  "image/png",
  "image/jpeg",
  "image/jpg",
  "image/webp",
  "image/avif",
];

const answerSchema = z.object({
  content: z.string().min(3, "answerContentMin").max(200, "answerContentMax"),
  isCorrect: z.boolean(),
});

const questionSchema = z
  .object({
    content: z
      .string()
      .min(3, "questionContentMin")
      .max(200, "questionContentMax"),
    type: z.enum(QuestionType),
    isRequired: z.boolean(),
    answers: z.array(answerSchema).max(6, "answersCountMax"),
    image: z.instanceof(File).optional(),
  })
  .superRefine((data, ctx) => {
    if (data.type !== QuestionType.Open && data.answers.length < 2) {
      ctx.addIssue({
        path: ["answers"],
        code: "custom",
        message: "answersCountMin",
      });
    }
    if (
      !data.answers.some((a) => a.isCorrect) &&
      data.type !== QuestionType.Open
    ) {
      ctx.addIssue({
        path: ["answers"],
        code: "custom",
        message: "oneCorrectAnswer",
      });
    }
    if (data.image) {
      if (!validImageTypes.includes(data.image.type)) {
        ctx.addIssue({
          path: ["image"],
          code: "custom",
          message: "imageFileType",
        });
      }

      if (data.image.size > maxFileSizeInBytes) {
        ctx.addIssue({
          path: ["image"],
          code: "custom",
          message: "imageFileSize",
        });
      }
    }
  });

export const formSchema = z
  .object({
    name: z.string().min(3, "formNameMin").max(200, "formNameMax"),
    description: z
      .string()
      .min(20, "formDescMin")
      .max(2000, "formDescMax")
      .or(z.literal("")),
    language: z.enum(Language),
    status: z.enum(FormStatus),
    shuffleVariant: z.enum(FormShuffleVariant).or(z.literal("NONE")),
    password: z
      .string()
      .min(8, "passwordMin")
      .max(200, "passwordMax")
      .or(z.literal("")),
    thanksMessage: z
      .string()
      .min(3, "thanksMessageMin")
      .max(500, "thanksMessageMax")
      .or(z.literal("")),
    estimatedDuration: z.enum(FormEstimatedDuration),
    allowsQuestionsPreview: z.boolean(),
    allowsGuestSubmissions: z.boolean(),
    saveSubmissions: z.boolean(),
    thumbnail: z.instanceof(File).optional(),
    questions: z
      .array(questionSchema)
      .min(3, "questionsCountMin")
      .max(100, "questionsCountMax"),
  })
  .superRefine((data, ctx) => {
    if (data.status === FormStatus.Private && !data.password) {
      ctx.addIssue({
        path: ["password"],
        code: "custom",
        message: "passwordRequired",
      });
    }
    if (!data.questions.some((q) => q.isRequired)) {
      ctx.addIssue({
        path: ["questions"],
        code: "custom",
        message: "oneQuestionRequired",
      });
    }
    if (data.thumbnail) {
      if (!validImageTypes.includes(data.thumbnail.type)) {
        ctx.addIssue({
          path: ["thumbnail"],
          code: "custom",
          message: "imageFileType",
        });
      }

      if (data.thumbnail.size > maxFileSizeInBytes) {
        ctx.addIssue({
          path: ["thumbnail"],
          code: "custom",
          message: "imageFileSize",
        });
      }
    }
  });
