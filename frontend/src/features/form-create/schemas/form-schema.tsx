import {
  FormEstimatedDuration,
  FormShuffleVariant,
  FormStatus,
  Language,
} from "@/core/types/form";
import { QuestionType } from "@/core/types/question";
import { Messages } from "next-intl";
import z from "zod";

type ErrorKey = keyof Messages["formCreatePage"]["errors"];
type TError = (errorKey: ErrorKey) => string;

export const validImageTypes: string[] = [
  "image/png",
  "image/jpeg",
  "image/jpg",
  "image/webp",
  "image/avif",
] as const;

const maxFileSizeInBytes = 10 * 1024 * 1024;

const getAnswerSchema = (t: TError) =>
  z.object({
    content: z
      .string()
      .trim()
      .min(3, t("answerContentMin"))
      .max(200, t("answerContentMax")),
    isCorrect: z.boolean(),
  });

const getQuestionSchema = (t: TError) =>
  z
    .object({
      content: z
        .string()
        .trim()
        .min(3, t("questionContentMin"))
        .max(200, t("questionContentMax")),
      type: z.enum(QuestionType),
      isRequired: z.boolean(),
      answers: z.array(getAnswerSchema(t)).max(6, t("answersCountMax")),
      image: z.instanceof(File).optional(),
    })
    .superRefine((data, ctx) => {
      if (data.type !== QuestionType.Open && data.answers.length < 2) {
        ctx.addIssue({
          path: ["answers"],
          code: "custom",
          message: t("answersCountMin"),
        });
      }
      if (
        !data.answers.some((a) => a.isCorrect) &&
        data.type !== QuestionType.Open
      ) {
        ctx.addIssue({
          path: ["answers"],
          code: "custom",
          message: t("oneCorrectAnswer"),
        });
      }
      if (data.image) {
        if (!validImageTypes.includes(data.image.type)) {
          ctx.addIssue({
            path: ["image"],
            code: "custom",
            message: t("imageFileType"),
          });
        }

        if (data.image.size > maxFileSizeInBytes) {
          ctx.addIssue({
            path: ["image"],
            code: "custom",
            message: t("imageFileSize"),
          });
        }
      }
    });

export const getFormSchema = (t: TError) =>
  z
    .object({
      name: z
        .string()
        .trim()
        .min(3, t("formNameMin"))
        .max(200, t("formNameMax")),
      description: z
        .string()
        .trim()
        .min(20, t("formDescMin"))
        .max(2000, t("formDescMax"))
        .or(z.literal("")),
      language: z.enum(Language),
      status: z.enum(FormStatus),
      shuffleVariant: z.enum(FormShuffleVariant).or(z.literal("NONE")),
      password: z
        .string()
        .trim()
        .min(8, t("passwordMin"))
        .max(200, t("passwordMax"))
        .or(z.literal("")),
      thanksMessage: z
        .string()
        .trim()
        .min(3, t("thanksMessageMin"))
        .max(500, t("thanksMessageMax"))
        .or(z.literal("")),
      estimatedDuration: z.enum(FormEstimatedDuration),
      allowsQuestionsPreview: z.boolean(),
      allowsGuestSubmissions: z.boolean(),
      saveSubmissions: z.boolean(),
      thumbnail: z.instanceof(File).optional(),
      questions: z
        .array(getQuestionSchema(t))
        .min(3, t("questionsCountMin"))
        .max(100, t("questionsCountMax")),
    })
    .superRefine((data, ctx) => {
      if (data.status === FormStatus.Private && !data.password) {
        ctx.addIssue({
          path: ["password"],
          code: "custom",
          message: t("passwordRequired"),
        });
      }
      if (!data.questions.some((q) => q.isRequired)) {
        ctx.addIssue({
          path: ["questions"],
          code: "custom",
          message: t("oneQuestionRequired"),
        });
      }
      if (data.thumbnail) {
        if (!validImageTypes.includes(data.thumbnail.type)) {
          ctx.addIssue({
            path: ["thumbnail"],
            code: "custom",
            message: t("imageFileType"),
          });
        }

        if (data.thumbnail.size > maxFileSizeInBytes) {
          ctx.addIssue({
            path: ["thumbnail"],
            code: "custom",
            message: t("imageFileSize"),
          });
        }
      }
    });
