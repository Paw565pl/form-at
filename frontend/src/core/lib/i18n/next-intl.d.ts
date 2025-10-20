import { ValidLocale } from "@/core/lib/i18n/request";
import messages from "@root/messages/en.json";

declare module "next-intl" {
  interface AppConfig {
    Locale: ValidLocale;
    Messages: typeof messages;
  }
}
