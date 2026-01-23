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

interface DeleteSubmissionAlertDialogProps {
  readonly isOpen: boolean;
  readonly isPending: boolean;
  readonly onClose: () => void;
  readonly onConfirm: () => void;
}

export const DeleteSubmissionAlertDialog = ({
  isOpen,
  isPending,
  onClose,
  onConfirm,
}: DeleteSubmissionAlertDialogProps) => {
  const t = useTranslations("submissionDetailsPage");

  return (
    <AlertDialog open={isOpen} onOpenChange={onClose}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t("deleteSubmission")}</AlertDialogTitle>
          <AlertDialogDescription>
            {t("deleteSubmissionDescription")}
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
            {t("deleteSubmission")}
          </Button>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
