"use client";

import { useTranslations } from "next-intl";

interface ErrorPageProps {
  readonly error: Error & { digest?: string };
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export const ErrorPage = (_: ErrorPageProps) => {
  const t = useTranslations("errorPage");

  return <p>{t("message")}</p>;
};
