import { auth } from "@/features/auth/config/auth-config";
import { SessionProvider } from "next-auth/react";
import { NextIntlClientProvider } from "next-intl";
import { NuqsAdapter } from "nuqs/adapters/next/app";
import { PropsWithChildren } from "react";

export const ServerProviders = async ({ children }: PropsWithChildren) => {
  const session = await auth();

  return (
    <SessionProvider session={session}>
      <NextIntlClientProvider>
        <NuqsAdapter>{children}</NuqsAdapter>
      </NextIntlClientProvider>
    </SessionProvider>
  );
};
