import { useTranslations } from "next-intl";

export const RootPage = () => {
  const t = useTranslations("rootPage");

  return <div>{t("message")}</div>;
};
