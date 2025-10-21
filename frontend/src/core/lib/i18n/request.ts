import { getRequestConfig } from "next-intl/server";
import { cookies, headers } from "next/headers";

const defaultLocale = "en";

const validLocales = [defaultLocale, "pl"] as const;
export type ValidLocale = (typeof validLocales)[number];

const isValidLocale = (value: string): value is ValidLocale =>
  (validLocales as readonly string[]).includes(value);

const getLocale = async () => {
  const cookiesSnapshot = await cookies();
  const savedLocale = cookiesSnapshot.get("locale")?.value;
  if (savedLocale && isValidLocale(savedLocale)) return savedLocale;

  const headersSnapshot = await headers();
  const preferredLocale = headersSnapshot
    .get("accept-language")
    ?.split(",")
    .at(0)
    ?.split(";")
    .at(0)
    ?.trim()
    .toLowerCase();
  if (!preferredLocale) return defaultLocale;

  const primaryLocale = preferredLocale.includes("-")
    ? preferredLocale.split("-").at(0) || ""
    : preferredLocale;
  return isValidLocale(primaryLocale) ? primaryLocale : defaultLocale;
};

export default getRequestConfig(async () => {
  const locale = await getLocale();
  const messages = (await import(`@root/messages/${locale}.json`)).default;

  return {
    locale,
    messages,
  };
});
