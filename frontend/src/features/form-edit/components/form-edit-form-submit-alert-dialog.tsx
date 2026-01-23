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
import { useTranslations } from "next-intl";

interface FormEditFormSubmitAlertDialogProps {
  readonly isFormPending: boolean;
  readonly uploadProgressPercent: number | null;
}

export const FormEditFormSubmitAlertDialog = ({
  isFormPending,
  uploadProgressPercent,
}: FormEditFormSubmitAlertDialogProps) => {
  const t = useTranslations("formEditPage");

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button
          type="button"
          className="ml-auto min-w-40"
          disabled={isFormPending}
        >
          {isFormPending ? <Spinner /> : <ICONS.save />}
          {isFormPending ? t("submitting") : t("submit")}
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
