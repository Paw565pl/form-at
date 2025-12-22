import { serverEnv } from "@/core/lib/env/server-env";
import type { NextConfig } from "next";
import createNextIntlPlugin from "next-intl/plugin";
import { RemotePattern } from "next/dist/shared/lib/image-config";

const getMinioRemotePattern = (): RemotePattern => {
  const { protocol, hostname, port, pathname } = new URL(serverEnv.MINIO_URL);

  return {
    protocol: protocol.replace(":", "") as "http" | "https",
    hostname,
    port,
    pathname: pathname + "/**",
  };
};

const nextConfig: NextConfig = {
  output: "standalone",
  transpilePackages: ["@t3-oss/env-nextjs", "@t3-oss/env-core"],
  typedRoutes: true,
  experimental: {
    reactCompiler: true,
  },
  images: {
    remotePatterns: [getMinioRemotePattern()],
  },
};

const withNextIntl = createNextIntlPlugin({
  requestConfig: "./src/core/lib/i18n/request.ts",
  experimental: {
    createMessagesDeclaration: "./messages/en.json",
  },
});

export default withNextIntl(nextConfig);
