/**
 * This file has been claimed for ownership from @oussemasahbeni/keycloakify-login-shadcn version 250004.0.20.
 * To relinquish ownership and restore this file to its original content, run the following command:
 *
 * $ npx keycloakify own --path "login/components/Template/Template.tsx" --revert
 */

import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { useKcContext } from "@/login/KcContext";
import { useInitializeTemplate } from "@/login/components/Template/useInitializeTemplate";
import { Logo } from "@/login/components/logo";
import { Languages } from "@/login/components/ui/Languages";
import { ModeToggle } from "@/login/components/ui/ThemeToggle";
import { useI18n } from "@/login/i18n";
import { kcSanitize } from "@keycloakify/login-ui/kcSanitize";
import { useKcClsx } from "@keycloakify/login-ui/useKcClsx";
import { useSetClassName } from "keycloakify/tools/useSetClassName";
import { RotateCcw, User } from "lucide-react";
import type { ReactNode } from "react";
import { useEffect } from "react";

export function Template(props: {
  displayInfo?: boolean;
  displayMessage?: boolean;
  displayRequiredFields?: boolean;
  headerNode: ReactNode;
  socialProvidersNode?: ReactNode;
  infoNode?: ReactNode;
  documentTitle?: string;
  bodyClassName?: string;
  children: ReactNode;
}) {
  const {
    displayInfo = false,
    displayMessage = true,
    headerNode,
    socialProvidersNode = null,
    infoNode = null,
    bodyClassName,
    children,
  } = props;

  const { kcContext } = useKcContext();

  const { auth, url, message, isAppInitiatedAction } = kcContext;

  const { msg, msgStr, enabledLanguages } = useI18n();

  const { kcClsx } = useKcClsx();

  useEffect(() => {
    document.title = "formAT";
  }, []);

  useSetClassName({
    qualifiedName: "html",
    className: kcClsx("kcHtmlClass"),
  });

  useSetClassName({
    qualifiedName: "body",
    className: bodyClassName ?? kcClsx("kcBodyClass"),
  });

  useInitializeTemplate();

  return (
    <main className="bg-background text-foreground flex min-h-screen flex-col font-sans text-pretty">
      <nav className="flex items-center justify-between gap-2 p-2">
        <div className="text-primary flex items-center text-lg font-semibold">
          <Logo />
          <h1>formAT</h1>
        </div>
        <div className="actions flex items-center gap-2">
          {kcContext.darkMode !== false && <ModeToggle />}
          {enabledLanguages.length > 1 && <Languages />}
        </div>
      </nav>

      <div className="flex w-full flex-col items-center gap-4 px-5 py-10 md:mt-20 lg:px-30">
        <Card className="flex min-w-1/2 flex-col gap-4 p-4">
          {(() => {
            const node = !(
              auth !== undefined &&
              auth.showUsername &&
              !auth.showResetCredentials
            ) ? (
              <h1 className="ml-2 text-xl font-bold">{headerNode}</h1>
            ) : (
              <div
                id="kc-username"
                className="flex items-center justify-between gap-2"
              >
                <div className="flex items-center gap-4">
                  <User className="text-muted-foreground size-6" />

                  <div className="flex flex-col gap-0.5">
                    <span className="text-muted-foreground text-xs font-normal">
                      {msgStr("attemptedUsernameLoggingInAs")}
                    </span>
                    <label
                      className="text-lg font-semibold"
                      id="kc-attempted-username"
                    >
                      {auth.attemptedUsername}
                    </label>
                  </div>
                </div>

                <TooltipProvider>
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <Button variant="outline" size="icon" asChild>
                        <a
                          id="reset-login"
                          href={url.loginRestartFlowUrl}
                          aria-label={msgStr("restartLoginTooltip")}
                        >
                          <RotateCcw className="size-4" />
                        </a>
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>
                      <p>{msg("restartLoginTooltip")}</p>
                    </TooltipContent>
                  </Tooltip>
                </TooltipProvider>
              </div>
            );

            return node;
          })()}

          <div id="kc-content" className="space-y-4">
            {displayMessage &&
              message !== undefined &&
              (message.type !== "warning" || !isAppInitiatedAction) && (
                <Alert variant={message.type}>
                  <AlertDescription>
                    <span
                      dangerouslySetInnerHTML={{
                        __html: kcSanitize(message.summary),
                      }}
                    />
                  </AlertDescription>
                </Alert>
              )}

            {socialProvidersNode}
            {children}

            {auth !== undefined && auth.showTryAnotherWayLink && (
              <form
                id="kc-select-try-another-way-form"
                action={url.loginAction}
                method="post"
              >
                <div className={kcClsx("kcFormGroupClass")}>
                  <input type="hidden" name="tryAnotherWay" value="on" />

                  <Button
                    type="button"
                    className="w-full"
                    variant="outline"
                    asChild
                  >
                    <a
                      href="#"
                      id="try-another-way"
                      onClick={(event) => {
                        document.forms[
                          "kc-select-try-another-way-form" as never
                        ].submit();
                        event.preventDefault();
                        return false;
                      }}
                    >
                      {msg("doTryAnotherWay")}
                    </a>
                  </Button>
                </div>
              </form>
            )}
            {displayInfo && (
              <div className="text-muted-foreground text-center text-sm">
                {infoNode}
              </div>
            )}
          </div>
        </Card>
      </div>
    </main>
  );
}
