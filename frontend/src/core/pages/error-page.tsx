"use client";

import { useTranslations } from "next-intl";

interface ErrorPageProps {
  readonly error: Error & { digest?: string };
}

export const ErrorPage = ({ error: _ }: ErrorPageProps) => {
  const t = useTranslations("errorPage");

  return <p>{t("message")}</p>;
};
