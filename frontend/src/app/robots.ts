import { getClientEnv } from "@/core/lib/env/client-env";
import type { MetadataRoute } from "next";

const robots = (): MetadataRoute.Robots => {
  return {
    rules: {
      userAgent: "*",
      allow: "/",
      disallow: "/users/",
    },
    sitemap: `${getClientEnv("NEXT_PUBLIC_APP_URL")}/sitemap.xml`,
  };
};

export default robots;
