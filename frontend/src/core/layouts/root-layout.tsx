import { NavBar } from "@/core/components/nav-bar/nav-bar";
import { Toaster } from "@/core/components/ui/sonner";
import { ClientProviders } from "@/core/providers/client-providers";
import { ServerProviders } from "@/core/providers/server-providers";
import { getLocale } from "next-intl/server";
import { PublicEnvScript } from "next-runtime-env";
import { PropsWithChildren } from "react";

export const RootLayout = async ({ children }: PropsWithChildren) => {
  const locale = await getLocale();

  return (
    <html lang={locale} suppressHydrationWarning>
      {/* eslint-disable-next-line @next/next/no-head-element */}
      <head>
        <PublicEnvScript />
      </head>
      <body>
        <ServerProviders>
          <ClientProviders>
            <NavBar />
            <main className="container mx-auto">{children}</main>
            <Toaster position="top-right" closeButton />
          </ClientProviders>
        </ServerProviders>
      </body>
    </html>
  );
};
