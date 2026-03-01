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
import { Button, buttonVariants } from "@/core/components/ui/button";
import { ICONS } from "@/core/config/icons";
import { useDeleteSubmission } from "@/features/submission-details/hooks/use-delete-submission";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

interface DeleteSubmissionAlertDialogProps {
  readonly formIdOrSlug: string;
  readonly submissionId: string;
}

export const DeleteSubmissionAlertDialog = ({
  formIdOrSlug,
  submissionId,
}: DeleteSubmissionAlertDialogProps) => {
  const t = useTranslations("submissionDetailsPage");
  const router = useRouter();

  const { mutate: deleteSubmission, isPending } = useDeleteSubmission(
    formIdOrSlug,
    submissionId,
  );

  const handleDelete = () =>
    deleteSubmission(undefined, {
      onSuccess: () => {
        toast.success(t("deleteSuccess"));
        router.replace(`/forms/${formIdOrSlug}/submissions`);
      },
      onError: () => {
        toast.error(t("deleteError"));
      },
    });

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button
          size="sm"
          variant="destructive"
          disabled={isPending}
          className="ml-auto"
        >
          <ICONS.delete />
          {t("deleteSubmission")}
        </Button>
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
            className={buttonVariants({ variant: "destructive" })}
          >
            {t("delete")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
