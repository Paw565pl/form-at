"use client";

import { Button } from "@/core/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/core/components/ui/dropdown-menu";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { Role } from "@/features/auth/types/role";
import { DeleteFormAlertDialog } from "@/features/form-details/components/delete-form-alert-dialog";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";

interface FormOptionsProps {
  readonly slug: string;
  readonly authorName: string | null;
}

export const FormOptions = ({ slug, authorName }: FormOptionsProps) => {
  const t = useTranslations("formDetailsPage.banner");
  const session = useSession();
  const router = useRouter();

  const user = session.data?.user;
  const isUserAuthor = !!authorName && user?.name === authorName;
  const isUserAdmin = user?.roles.includes(Role.ADMIN);

  if (!isUserAuthor && !isUserAdmin) return null;

  return (
    <section>
      <DropdownMenu>
        <Tooltip>
          <TooltipTrigger asChild>
            <DropdownMenuTrigger asChild>
              <Button aria-label={t("moreOptions")} size="icon">
                <ICONS.more />
              </Button>
            </DropdownMenuTrigger>
          </TooltipTrigger>
          <TooltipContent>
            <span>{t("moreOptions")}</span>
          </TooltipContent>
        </Tooltip>

        <DropdownMenuContent align="end">
          {isUserAuthor && (
            <>
              <DropdownMenuItem
                onClick={() => router.push(`/forms/${slug}/submissions`)}
              >
                <ICONS.submissions />
                {t("viewSubmissions")}
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => router.push(`/forms/${slug}/edit`)}
              >
                <ICONS.edit />
                {t("edit")}
              </DropdownMenuItem>
            </>
          )}

          <DeleteFormAlertDialog slug={slug} />
        </DropdownMenuContent>
      </DropdownMenu>
    </section>
  );
};
