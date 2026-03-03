import { useTranslations } from "next-intl";
import * as z from "zod";

type TranslateError = ReturnType<
  typeof useTranslations<"formDetailsPage.privateForm">
>;

export const getPrivateFormSchema = (t: TranslateError) =>
  z.object({
    password: z
      .string()
      .trim()
      .min(8, t("errors.contentMin", { count: "8" }))
      .max(200, t("errors.contentMax", { count: "200" })),
  });
