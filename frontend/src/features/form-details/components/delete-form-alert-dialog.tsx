import {
  AlertDialog,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/core/components/ui/alert-dialog";
import { Button } from "@/core/components/ui/button";
import { useTranslations } from "next-intl";

interface DeleteFormAlertDialogProps {
  readonly isOpen: boolean;
  readonly isPending: boolean;
  readonly onClose: () => void;
  readonly onConfirm: () => void;
}

export const DeleteFormAlertDialog = ({
  isOpen,
  isPending,
  onClose,
  onConfirm,
}: DeleteFormAlertDialogProps) => {
  const t = useTranslations("formDetailsPage.banner");

  return (
    <AlertDialog open={isOpen} onOpenChange={onClose}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t("deleteDialogTitle")}</AlertDialogTitle>
          <AlertDialogDescription>
            {t("deleteDialogDescription")}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <Button variant="outline" onClick={onClose}>
            {t("cancel")}
          </Button>
          <Button
            variant="destructive"
            onClick={onConfirm}
            disabled={isPending}
          >
            {t("delete")}
          </Button>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
