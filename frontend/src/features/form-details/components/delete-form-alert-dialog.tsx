"use client";

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/core/components/ui/alert-dialog";
import { buttonVariants } from "@/core/components/ui/button";
import { DropdownMenuItem } from "@/core/components/ui/dropdown-menu";
import { ICONS } from "@/core/config/icons";
import { useDeleteForm } from "@/features/form-details/hooks/use-delete-form";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";

interface DeleteFormAlertDialogProps {
  readonly slug: string;
}

export const DeleteFormAlertDialog = ({ slug }: DeleteFormAlertDialogProps) => {
  const router = useRouter();
  const t = useTranslations("formDetailsPage.banner");

  const [isOpen, setIsOpen] = useState(false);
  const { mutate: deleteForm, isPending } = useDeleteForm(slug);

  const handleDelete = () =>
    deleteForm(undefined, {
      onSuccess: () => {
        setIsOpen(false);
        toast.success(t("deleteSuccess"));
        router.replace("/forms");
      },
      onError: () => {
        toast.error(t("deleteError"));
      },
    });

  return (
    <AlertDialog open={isOpen} onOpenChange={setIsOpen}>
      <AlertDialogTrigger asChild>
        <DropdownMenuItem
          disabled={isPending}
          variant="destructive"
          onSelect={(e) => {
            e.preventDefault();
          }}
        >
          <ICONS.delete />
          {t("delete")}
        </DropdownMenuItem>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t("deleteDialogTitle")}</AlertDialogTitle>
          <AlertDialogDescription>
            {t("deleteDialogDescription")}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{t("cancel")}</AlertDialogCancel>
          <AlertDialogAction
            onClick={handleDelete}
            disabled={isPending}
            className={buttonVariants({ variant: "destructive" })}
          >
            {t("delete")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
