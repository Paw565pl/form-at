import { Button } from "@/core/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/core/components/ui/dropdown-menu";
import { ICONS } from "@/core/config/icons";
import { Role } from "@/features/auth/types/role";
import { DeleteCommentAlertDialog } from "@/features/form-details/comments/components/delete-comment-dialog";
import { useDeleteComment } from "@/features/form-details/comments/hooks/use-delete-comment";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

interface CommentOptionsProps {
  readonly formIdOrSlug: string;
  readonly commentId: string;
  readonly authorName: string;
  readonly onEdit?: () => void;
}

export const CommentOptions = ({
  formIdOrSlug,
  commentId,
  authorName,
  onEdit,
}: CommentOptionsProps) => {
  const t = useTranslations("formDetailsPage.comments");
  const session = useSession();
  const { mutate: deleteComment, isPending } = useDeleteComment(
    formIdOrSlug,
    commentId,
  );
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  const user = session.data?.user;
  const isUserAuthor = user?.name === authorName;
  const isUserAdmin = user?.roles.includes(Role.ADMIN);

  if (!isUserAuthor && !isUserAdmin) return null;

  const handleDelete = () =>
    deleteComment(undefined, {
      onSuccess: () => {
        toast.success(t("deleteSuccess"));
        setIsDeleteDialogOpen(false);
      },
      onError: () => {
        toast.error(t("deleteError"));
      },
    });

  return (
    <span className="absolute top-4 right-4">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button aria-label={t("moreOptions")} size="icon-sm" variant="ghost">
            <ICONS.more />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          {isUserAuthor && (
            <DropdownMenuItem onClick={onEdit}>
              <ICONS.edit />
              {t("edit")}
            </DropdownMenuItem>
          )}
          <DropdownMenuItem
            onClick={() => setIsDeleteDialogOpen(true)}
            disabled={isPending}
            variant="destructive"
          >
            <ICONS.delete />
            {t("delete")}
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <DeleteCommentAlertDialog
        isOpen={isDeleteDialogOpen}
        isPending={isPending}
        onClose={() => setIsDeleteDialogOpen(false)}
        onConfirm={handleDelete}
      />
    </span>
  );
};
