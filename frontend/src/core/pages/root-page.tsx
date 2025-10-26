import { AuthButton } from "@/core/components/auth-button/auth-button";
import { useTranslations } from "next-intl";

export const RootPage = () => {
  const t = useTranslations("rootPage");

  return (
    <>
      <AuthButton />
      <div>{t("message")}</div>
    </>
  );
};
