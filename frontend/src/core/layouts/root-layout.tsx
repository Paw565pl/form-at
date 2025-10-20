import { Providers } from "@/core/providers/providers";
import { NextIntlClientProvider } from "next-intl";
import { getLocale } from "next-intl/server";
import { NuqsAdapter } from "nuqs/adapters/next/app";
import { PropsWithChildren } from "react";

export const RootLayout = async ({ children }: PropsWithChildren) => {
  const locale = await getLocale();

  return (
    <html lang={locale} suppressHydrationWarning>
      <body>
        <NextIntlClientProvider>
          <NuqsAdapter>
            <Providers>{children}</Providers>
          </NuqsAdapter>
        </NextIntlClientProvider>
      </body>
    </html>
  );
};
