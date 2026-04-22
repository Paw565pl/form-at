/**
 * This file has been claimed for ownership from @oussemasahbeni/keycloakify-login-shadcn version 250004.0.20.
 * To relinquish ownership and restore this file to its original content, run the following command:
 *
 * $ npx keycloakify own --path "login/i18n.ts" --revert
 */

import type { ThemeName } from "@/kc.gen";
import { i18nBuilder } from "@keycloakify/login-ui/i18n";

/** @see: https://docs.keycloakify.dev/features/i18n */
const { I18nProvider, useI18n } = i18nBuilder
  .withThemeName<ThemeName>()
  .withCustomTranslations({
    en: {
      welcomeMessage: "Welcome to formAT",
      loginAccountTitle: "Login to your account",
      registerTitle: "Create a new account",
      email: "Email",
      noAccount: "Don't have an account?",
      doRegister: "Sign up",
      "organization.selectTitle": "Choose Your Organization",
      "organization.pickPlaceholder": "Pick an organization to continue",
      "identity-provider-login-last-used": "Last used",
      attemptedUsernameLoggingInAs: "Logging in as",
    },
    pl: {
      welcomeMessage: "Witamy w formAT",
      loginAccountTitle: "Zaloguj się",
      registerTitle: "Utwórz nowe konto",
      email: "Email",
      noAccount: "Nie masz konta?",
      doRegister: "Zarejestruj się",
      "organization.selectTitle": "Wybierz swoją organizację",
      "organization.pickPlaceholder": "Wybierz organizację, aby kontynuować",
      "identity-provider-login-last-used": "Ostatnio używane",
      attemptedUsernameLoggingInAs: "Logowanie jako",
    },
  })
  .build();

export { I18nProvider, useI18n };
