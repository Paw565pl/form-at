import { AuthButton } from "@/core/components/auth-button/auth-button";
import { useTranslations } from "next-intl";

export const RootPage = () => {
  const t = useTranslations("RootPage");

  return (
    <main>
      <AuthButton />
      <div>{t("message")}</div>
    </main>
  );
};
