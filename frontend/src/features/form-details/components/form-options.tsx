"use client";

import { Button } from "@/core/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/core/components/ui/dropdown-menu";
import { ICONS } from "@/core/config/icons";
import { Role } from "@/features/auth/types/role";
import { DeleteForm } from "@/features/form-details/components/delete-form";
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
        <DropdownMenuTrigger asChild>
          <Button aria-label={t("moreOptions")} size={"icon-sm"}>
            <ICONS.more />
          </Button>
        </DropdownMenuTrigger>

        <DropdownMenuContent align="end">
          {isUserAuthor && (
            <>
              <DropdownMenuItem
                onClick={() => router.push(`/forms/${slug}/submissions`)}
              >
                <ICONS.viewSubmissions />
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

          <DeleteForm slug={slug} />
        </DropdownMenuContent>
      </DropdownMenu>
    </section>
  );
};
