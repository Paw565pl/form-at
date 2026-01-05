import { ErrorKey } from "@/features/form-details/comments/schemas/comment-schema";
import { useTranslations } from "next-intl";
import { FieldError } from "react-hook-form";

export const getTranslatedErrors = (
  translator: ReturnType<typeof useTranslations>,
  error?: FieldError,
): { message: string }[] => {
  return error ? [{ message: translator(`${error.message as ErrorKey}`) }] : [];
};
