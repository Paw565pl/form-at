import {
  FormEstimatedDuration,
  FormShuffleVariant,
  FormStatus,
  Language,
} from "@/core/types/form";
import { QuestionType } from "@/core/types/question";
import {
  MAX_FILE_SIZE_IN_BYTES,
  VALID_INPUT_IMAGE_CONTENT_TYPES,
} from "@/features/form/constants/image";
import { useTranslations } from "next-intl";
import * as z from "zod";

type TranslateError = ReturnType<typeof useTranslations<"formBaseForm">>;

const getAnswerSchema = (t: TranslateError) =>
  z.object({
    content: z
      .string()
      .trim()
      .min(3, t("errors.answerContentMin", { count: "3" }))
      .max(200, t("errors.answerContentMax", { count: "200" })),
    isCorrect: z.boolean(),
  });

const getQuestionSchema = (t: TranslateError) =>
  z
    .object({
      content: z
        .string()
        .trim()
        .min(3, t("errors.questionContentMin", { count: "3" }))
        .max(200, t("errors.questionContentMax", { count: "200" })),
      type: z.enum(QuestionType),
      isRequired: z.boolean(),
      answers: z
        .array(getAnswerSchema(t))
        .max(6, t("errors.answersCountMax", { count: "6" })),
      // file is new file selected by user
      // string is existing url
      image: z
        .file()
        .min(1, t("errors.imageFileNonEmpty"))
        .max(
          MAX_FILE_SIZE_IN_BYTES,
          t("errors.imageFileSize", {
            size: `${MAX_FILE_SIZE_IN_BYTES / 1024 / 1024} MB`,
          }),
        )
        .mime([...VALID_INPUT_IMAGE_CONTENT_TYPES])
        .or(z.string().trim())
        .optional(),
    })
    .superRefine((data, ctx) => {
      if (data.type !== QuestionType.Open && data.answers.length < 2) {
        ctx.addIssue({
          path: ["answers"],
          code: "custom",
          message: t("errors.answersCountMin", { count: "2" }),
        });
      }

      if (
        !data.answers.some((a) => a.isCorrect) &&
        data.type !== QuestionType.Open
      ) {
        ctx.addIssue({
          path: ["answers"],
          code: "custom",
          message: t("errors.oneCorrectAnswer"),
        });
      }
    });

export const getFormSchema = (t: TranslateError) =>
  z
    .object({
      name: z
        .string()
        .trim()
        .min(3, t("errors.formNameMin", { count: "3" }))
        .max(200, t("errors.formNameMax", { count: "200" })),
      description: z
        .string()
        .trim()
        .min(20, t("errors.formDescMin", { count: "20" }))
        .max(2000, t("errors.formDescMax", { count: "2000" }))
        .or(z.literal("")),
      language: z.enum(Language),
      status: z.enum(FormStatus),
      shuffleVariant: z.enum(FormShuffleVariant).or(z.literal("NONE")),
      password: z
        .string()
        .trim()
        .min(8, t("errors.passwordMin", { count: "8" }))
        .max(200, t("errors.passwordMax", { count: "200" }))
        .or(z.literal("")),
      thanksMessage: z
        .string()
        .trim()
        .min(3, t("errors.thanksMessageMin", { count: "3" }))
        .max(500, t("errors.thanksMessageMax", { count: "500" }))
        .or(z.literal("")),
      estimatedDuration: z.enum(FormEstimatedDuration),
      allowsQuestionsPreview: z.boolean(),
      allowsGuestSubmissions: z.boolean(),
      saveSubmissions: z.boolean(),
      showAnswersFeedback: z.boolean(),
      // file is new file selected by user
      // string is existing url
      thumbnail: z
        .file()
        .min(1, t("errors.imageFileNonEmpty"))
        .max(
          MAX_FILE_SIZE_IN_BYTES,
          t("errors.imageFileSize", {
            size: `${MAX_FILE_SIZE_IN_BYTES / 1024 / 1024} MB`,
          }),
        )
        .mime([...VALID_INPUT_IMAGE_CONTENT_TYPES])
        .or(z.string().trim())
        .optional(),
      questions: z
        .array(getQuestionSchema(t))
        .min(3, t("errors.questionsCountMin", { count: "3" }))
        .max(100, t("errors.questionsCountMax", { count: "100" })),
    })
    .superRefine((data, ctx) => {
      if (data.status === FormStatus.Private && !data.password) {
        ctx.addIssue({
          path: ["password"],
          code: "custom",
          message: t("errors.passwordRequired"),
        });
      }

      if (!data.questions.some((q) => q.isRequired)) {
        ctx.addIssue({
          path: ["questions"],
          code: "custom",
          message: t("errors.oneQuestionRequired"),
        });
      }
    });
