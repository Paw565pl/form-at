/**
 * This file has been claimed for ownership from @oussemasahbeni/keycloakify-login-shadcn version 250004.0.20.
 * To relinquish ownership and restore this file to its original content, run the following command:
 *
 * $ npx keycloakify own --path "login/components/ui/Langauges.tsx" --revert
 */

import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { useI18n } from "@/login/i18n";
import { Button } from "../../../components/ui/button";
import { Check, Globe } from "lucide-react";

export function Languages() {
  const { msgStr, currentLanguage, enabledLanguages } = useI18n();

  return (
    <div>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="outline" size="icon-sm" aria-label={msgStr("languages")} aria-haspopup="true" aria-expanded="false" aria-controls="language-switch1">
            <Globe />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent id="language-switch1" role="menu" className="max-h-72 overflow-y-auto">
          {enabledLanguages.map(({ languageTag, label, href }, i) => {
            const isActive = languageTag === currentLanguage.languageTag;

            return (
              <DropdownMenuItem key={languageTag} asChild>
                <a role="menuitem" id={`language-${i + 1}`} href={href} className="justify-between">
                  {label}
                  {isActive && <Check />}
                </a>
              </DropdownMenuItem>
            );
          })}
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}
