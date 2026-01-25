import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/core/components/ui/alert-dialog";
import { Button } from "@/core/components/ui/button";
import { ICONS } from "@/core/config/icons";
import { useDeleteSubmission } from "@/features/submission-details/hooks/use-delete-submission";
import { AlertDialogCancel } from "@radix-ui/react-alert-dialog";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

interface DeleteSubmissionProps {
  readonly formIdOrSlug: string;
  readonly submissionId: string;
}

export const DeleteSubmission = ({
  formIdOrSlug,
  submissionId,
}: DeleteSubmissionProps) => {
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
          <AlertDialogTitle>{t("deleteSubmission")}</AlertDialogTitle>
          <AlertDialogDescription>
            {t("deleteSubmissionDescription")}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{t("cancel")}</AlertDialogCancel>
          <AlertDialogAction onClick={handleDelete}>
            {t("deleteSubmission")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
