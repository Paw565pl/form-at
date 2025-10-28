import { auth } from "@/features/auth/config/auth-config";
import { SessionProvider } from "next-auth/react";
import { NextIntlClientProvider } from "next-intl";
import { NuqsAdapter } from "nuqs/adapters/next/app";
import { PropsWithChildren } from "react";

const sessionRefetchInterval = 60 * 30; // 30 minutes in seconds

export const ServerProviders = async ({ children }: PropsWithChildren) => {
  const session = await auth();

  return (
    <SessionProvider session={session} refetchInterval={sessionRefetchInterval}>
      <NextIntlClientProvider>
        <NuqsAdapter>{children}</NuqsAdapter>
      </NextIntlClientProvider>
    </SessionProvider>
  );
};
