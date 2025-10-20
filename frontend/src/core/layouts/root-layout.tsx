import { Providers } from "@/core/providers/providers";
import { PropsWithChildren } from "react";

export const RootLayout = ({ children }: PropsWithChildren) => {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
};
