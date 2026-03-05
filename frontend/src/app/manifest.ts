import type { MetadataRoute } from "next";
import { getTranslations } from "next-intl/server";

const manifest = async (): Promise<MetadataRoute.Manifest> => {
  const t = await getTranslations("homePage");
  return {
    name: "formAT",
    short_name: "formAT",
    description: t("appDescription"),
    icons: [
      {
        src: "/web-app-manifest-192x192.png",
        sizes: "192x192",
        type: "image/png",
        purpose: "maskable",
      },
      {
        src: "/web-app-manifest-512x512.png",
        sizes: "512x512",
        type: "image/png",
        purpose: "maskable",
      },
    ],
    theme_color: "#1F7771",
    background_color: "#121212",
    display: "standalone",
  };
};

export default manifest;
