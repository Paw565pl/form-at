import { NavBar } from "@/core/components/nav-bar/nav-bar";
import { Toaster } from "@/core/components/ui/sonner";
import { ClientProviders } from "@/core/providers/client-providers";
import { ServerProviders } from "@/core/providers/server-providers";
import { getLocale } from "next-intl/server";
import { PublicEnvScript } from "next-runtime-env";
import { Cascadia_Mono, Montserrat, Noto_Serif } from "next/font/google";
import { PropsWithChildren } from "react";

const noto_serif = Noto_Serif({
  subsets: ["latin"],
  variable: "--font-serif",
  display: "swap",
});

const montserrat = Montserrat({
  subsets: ["latin"],
  variable: "--font-sans",
  display: "swap",
});

const cascadia_mono = Cascadia_Mono({
  subsets: ["latin"],
  variable: "--font-mono",
  display: "swap",
});

export const RootLayout = async ({ children }: PropsWithChildren) => {
  const locale = await getLocale();

  return (
    <html
      lang={locale}
      className={`${noto_serif.variable} ${montserrat.variable} ${cascadia_mono.variable} antialiased`}
      suppressHydrationWarning
    >
      {/* eslint-disable-next-line @next/next/no-head-element */}
      <head>
        <PublicEnvScript />
      </head>
      <body className="flex min-h-screen flex-col">
        <ServerProviders>
          <ClientProviders>
            <NavBar />
            <main className="container mx-auto flex flex-1 flex-col">
              {children}
            </main>
            <Toaster position="top-right" closeButton />
          </ClientProviders>
        </ServerProviders>
      </body>
    </html>
  );
};
