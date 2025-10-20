import { useTranslations } from "next-intl";

export const RootPage = () => {
  const t = useTranslations("RootPage");

  return <div>{t("message")}</div>;
};
