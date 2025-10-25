import { ClientProviders } from "@/core/providers/client-providers";
import { ServerProviders } from "@/core/providers/server-providers";
import { getLocale } from "next-intl/server";
import { PropsWithChildren } from "react";

export const RootLayout = async ({ children }: PropsWithChildren) => {
  const locale = await getLocale();

  return (
    <html lang={locale} suppressHydrationWarning>
      <body>
        <ServerProviders>
          <ClientProviders>{children}</ClientProviders>
        </ServerProviders>
      </body>
    </html>
  );
};
