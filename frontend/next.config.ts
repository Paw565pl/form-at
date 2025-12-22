import { serverEnv } from "@/core/lib/env/server-env";
import type { NextConfig } from "next";
import createNextIntlPlugin from "next-intl/plugin";

const nextConfig: NextConfig = {
  output: "standalone",
  transpilePackages: ["@t3-oss/env-nextjs", "@t3-oss/env-core"],
  typedRoutes: true,
  experimental: {
    reactCompiler: true,
  },
  images: {
    remotePatterns: [new URL(serverEnv.MINIO_URL + "/**")],
  },
};

const withNextIntl = createNextIntlPlugin({
  requestConfig: "./src/core/lib/i18n/request.ts",
  experimental: {
    createMessagesDeclaration: "./messages/en.json",
  },
});

export default withNextIntl(nextConfig);
