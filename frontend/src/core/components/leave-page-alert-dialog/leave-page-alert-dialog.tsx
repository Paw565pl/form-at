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
} from "@/core/components/ui/alert-dialog";
import { buttonVariants } from "@/core/components/ui/button";
import { useTranslations } from "next-intl";
import { useNavigationGuard } from "next-navigation-guard";

interface LeavePageAlertDialogProps {
  readonly navGuard: ReturnType<typeof useNavigationGuard>;
}

export const LeavePageAlertDialog = ({
  navGuard,
}: LeavePageAlertDialogProps) => {
  const t = useTranslations("components.leavePageDialog");

  return (
    <AlertDialog open={navGuard.active} onOpenChange={navGuard.reject}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t("title")}</AlertDialogTitle>
          <AlertDialogDescription>{t("description")}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel onClick={navGuard.reject}>
            {t("cancel")}
          </AlertDialogCancel>
          <AlertDialogAction
            onClick={navGuard.accept}
            className={buttonVariants({ variant: "destructive" })}
          >
            {t("leave")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
