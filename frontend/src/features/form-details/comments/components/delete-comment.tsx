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
import { DropdownMenuItem } from "@/core/components/ui/dropdown-menu";
import { ICONS } from "@/core/config/icons";
import { useDeleteComment } from "@/features/form-details/comments/hooks/use-delete-comment";
import { useTranslations } from "next-intl";
import { toast } from "sonner";

interface DeleteCommentAlertDialogProps {
  readonly formIdOrSlug: string;
  readonly commentId: string;
}

export const DeleteComment = ({
  formIdOrSlug,
  commentId,
}: DeleteCommentAlertDialogProps) => {
  const t = useTranslations("formDetailsPage.comments");

  const { mutate: deleteComment, isPending } = useDeleteComment(
    formIdOrSlug,
    commentId,
  );

  const handleDelete = () =>
    deleteComment(undefined, {
      onSuccess: () => {
        toast.success(t("deleteSuccess"));
      },
      onError: () => {
        toast.error(t("deleteError"));
      },
    });

  return (
    <AlertDialog>
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
          <AlertDialogAction onClick={handleDelete} disabled={isPending}>
            {t("delete")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
