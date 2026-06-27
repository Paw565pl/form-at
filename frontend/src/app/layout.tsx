import "@/app/globals.css";
import { RootLayout } from "@/core/layouts/root-layout";
import { getClientEnv } from "@/core/lib/env/client-env";
import type { Metadata } from "next";
import { getLocale, getTranslations } from "next-intl/server";

export const generateMetadata = async (): Promise<Metadata> => {
  const t = await getTranslations("homePage");
  const locale = await getLocale();
  const appName = "formAT";

  return {
    title: appName,
    description: t("appDescription"),
    metadataBase: getClientEnv("NEXT_PUBLIC_APP_URL"),

    openGraph: {
      type: "website",
      siteName: appName,
      title: appName,
      description: t("appDescription"),
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
