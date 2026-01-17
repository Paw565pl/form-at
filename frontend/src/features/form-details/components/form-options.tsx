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
import { DeleteFormAlertDialog } from "@/features/form-details/components/delete-form-alert-dialog";
import { useDeleteForm } from "@/features/form-details/hooks/use-delete-form";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";

interface FormOptionsProps {
  readonly slug: string;
  readonly authorName: string | null;
}

export const FormOptions = ({ slug, authorName }: FormOptionsProps) => {
  const t = useTranslations("formDetailsPage.banner");
  const session = useSession();
  const router = useRouter();

  const { mutate: deleteForm, isPending } = useDeleteForm(slug);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  const user = session.data?.user;
  const isUserAuthor = !!authorName && user?.name === authorName;
  const isUserAdmin = user?.roles.includes(Role.ADMIN);

  if (!isUserAuthor && !isUserAdmin) return null;

  const handleDelete = () =>
    deleteForm(undefined, {
      onSuccess: () => {
        toast.success(t("deleteSuccess"));
        setIsDeleteDialogOpen(false);
        router.replace("/forms");
      },
      onError: () => {
        toast.error(t("deleteError"));
      },
    });

  return (
    <section>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button aria-label={t("moreOptions")}>
            <ICONS.more />
          </Button>
        </DropdownMenuTrigger>

        <DropdownMenuContent align="end">
          {isUserAuthor && (
            <DropdownMenuItem
              onClick={() => router.replace(`/forms/${slug}/edit`)}
            >
              <ICONS.edit />
              {t("edit")}
            </DropdownMenuItem>
          )}

          <DropdownMenuItem
            onClick={() => setIsDeleteDialogOpen(true)}
            disabled={isPending}
            variant="destructive"
          >
            <ICONS.delete />
            {t("delete")}
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <DeleteFormAlertDialog
        isOpen={isDeleteDialogOpen}
        isPending={isPending}
        onClose={() => setIsDeleteDialogOpen(false)}
        onConfirm={handleDelete}
      />
    </section>
  );
};
