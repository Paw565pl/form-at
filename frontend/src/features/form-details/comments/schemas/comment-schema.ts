import { useTranslations } from "next-intl";
import z from "zod";

type TranslateError = ReturnType<
  typeof useTranslations<"formDetailsPage.comments">
>;

export const getCommentSchema = (t: TranslateError) =>
  z.object({
    content: z
      .string()
      .trim()
      .min(3, t("errors.contentMin", { count: "3" }))
      .max(500, t("errors.contentMax", { count: "500" })),
  });
