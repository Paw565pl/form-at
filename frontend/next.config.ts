import { serverEnv } from "@/core/lib/env/server-env";
import type { NextConfig } from "next";
import createNextIntlPlugin from "next-intl/plugin";
import { RemotePattern } from "next/dist/shared/lib/image-config";

const getS3RemotePattern = (): RemotePattern => {
  if (!serverEnv.S3_URL) return { hostname: "localhost" };

  const url = new URL(serverEnv.S3_URL);
  url.pathname += "/**";

  return {
    protocol: url.protocol.replace(":", "") as "http" | "https",
    hostname: url.hostname,
    port: url.port,
    pathname: url.pathname,
  };
};

const nextConfig: NextConfig = {
  output: "standalone",
  transpilePackages: ["@t3-oss/env-nextjs", "@t3-oss/env-core"],
  reactCompiler: true,
  typedRoutes: true,
  images: {
    remotePatterns: [getS3RemotePattern()],
    dangerouslyAllowLocalIP: true,
  },
};

const withNextIntl = createNextIntlPlugin({
  requestConfig: "./src/core/lib/i18n/request.ts",
  experimental: {
    createMessagesDeclaration: "./messages/en.json",
    messages: {
      path: "./messages",
      locales: "infer",
      format: "json",
      precompile: true,
    },
  },
});

export default withNextIntl(nextConfig);
