import "@/app/globals.css";
import { RootLayout } from "@/core/layouts/root-layout";
import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";

export const generateMetadata = async (): Promise<Metadata> => {
  const t = await getTranslations("homePage");
  return {
    title: "formAT",
    description: t("appDescription"),
  };
};

export default RootLayout;
