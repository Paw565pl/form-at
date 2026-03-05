import { NavBar } from "@/core/components/nav-bar/nav-bar";
import { Toaster } from "@/core/components/ui/sonner";
import { ClientProviders } from "@/core/providers/client-providers";
import { ServerProviders } from "@/core/providers/server-providers";
import { getLocale } from "next-intl/server";
import { PublicEnvScript } from "next-runtime-env";
import { Montserrat, Noto_Serif, Roboto_Mono } from "next/font/google";
import { PropsWithChildren } from "react";

const noto_serif = Noto_Serif({
  subsets: ["latin-ext"],
  variable: "--font-serif",
  display: "swap",
});

const montserrat = Montserrat({
  subsets: ["latin-ext"],
  variable: "--font-sans",
  display: "swap",
});

const roboto_mono = Roboto_Mono({
  subsets: ["latin-ext"],
  variable: "--font-mono",
  display: "swap",
});

export const RootLayout = async ({ children }: PropsWithChildren) => {
  const locale = await getLocale();

  return (
    <html
      lang={locale}
      className={`${noto_serif.variable} ${montserrat.variable} ${roboto_mono.variable} antialiased`}
      suppressHydrationWarning
    >
      {/* eslint-disable-next-line @next/next/no-head-element */}
      <head>
        <PublicEnvScript />
      </head>
      <body className="bg-background text-foreground flex min-h-screen flex-col font-sans text-pretty">
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
