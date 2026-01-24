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
import { Button } from "@/core/components/ui/button";
import { Spinner } from "@/core/components/ui/spinner";
import { ICONS } from "@/core/config/icons";
import { FormBaseFormSubmitComponentProps } from "@/features/form/components/form-base-form";
import { useTranslations } from "next-intl";
import { useState } from "react";

export const FormEditFormSubmitAlertDialog = ({
  isPending,
  uploadProgressPercent,
  onDialogOpen,
}: FormBaseFormSubmitComponentProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const t = useTranslations("formEditPage");

  const handleOpenChange = () => {
    if (isOpen) setIsOpen(false);
    else onDialogOpen(() => setIsOpen(true));
  };

  return (
    <AlertDialog open={isOpen} onOpenChange={handleOpenChange}>
      <AlertDialogTrigger asChild>
        <Button type="button" className="ml-auto min-w-40" disabled={isPending}>
          {isPending ? <Spinner /> : <ICONS.save />}
          {isPending ? t("submitting") : t("submit")}
          {uploadProgressPercent ? ` (${uploadProgressPercent}%)` : ""}
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t("updateDialogTitle")}</AlertDialogTitle>
          <AlertDialogDescription>
            {t("updateDialogDescription")}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{t("cancel")}</AlertDialogCancel>
          <AlertDialogAction type="submit" form="form-base-form">
            {t("continue")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
