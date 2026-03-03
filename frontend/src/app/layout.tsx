import "@/app/globals.css";
import { RootLayout } from "@/core/layouts/root-layout";
import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("homePage");
  return {
    title: "FormAt",
    description: t("appDescription"),
  };
}

export default RootLayout;
