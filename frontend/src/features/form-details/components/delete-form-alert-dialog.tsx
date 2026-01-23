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
import { useTranslations } from "next-intl";

interface DeleteFormAlertDialogProps {
  readonly isOpen: boolean;
  readonly isPending: boolean;
  readonly onConfirm: () => void;
  readonly onClose: () => void;
}

export const DeleteFormAlertDialog = ({
  isOpen,
  isPending,
  onConfirm,
  onClose,
}: DeleteFormAlertDialogProps) => {
  const t = useTranslations("formDetailsPage.banner");

  return (
    <AlertDialog open={isOpen}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t("deleteDialogTitle")}</AlertDialogTitle>
          <AlertDialogDescription>
            {t("deleteDialogDescription")}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel onClick={onClose}>{t("cancel")}</AlertDialogCancel>
          <AlertDialogAction onClick={onConfirm} disabled={isPending}>
            {t("delete")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
