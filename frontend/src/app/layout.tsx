import "@/app/globals.css";
import { RootLayout } from "@/core/layouts/root-layout";
import { getClientEnv } from "@/core/lib/env/client-env";
import type { Metadata } from "next";
import { getLocale, getTranslations } from "next-intl/server";

export const generateMetadata = async (): Promise<Metadata> => {
  const t = await getTranslations("homePage.app");
  const locale = await getLocale();

  return {
    title: t("name"),
    description: t("description"),
    metadataBase: getClientEnv("NEXT_PUBLIC_APP_URL"),

    openGraph: {
      type: "website",
      siteName: t("name"),
      title: t("name"),
      description: t("description"),
      locale,
    },

    twitter: {
      card: "summary_large_image",
    },

    formatDetection: {
      telephone: false,
    },
  };
};

export default RootLayout;
