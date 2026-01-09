import { Messages } from "next-intl";
import z from "zod";

type ErrorKey = keyof Messages["formDetailsPage"]["comments"]["errors"];

export const getCommentSchema = (t: (errorKey: ErrorKey) => string) =>
  z.object({
    content: z
      .string()
      .trim()
      .min(3, t("contentMin"))
      .max(500, t("contentMax")),
  });
