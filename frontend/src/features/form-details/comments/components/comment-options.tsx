import { Button } from "@/core/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/core/components/ui/dropdown-menu";
import { ICONS } from "@/core/config/icons";
import { Role } from "@/features/auth/types/role";
import { DeleteCommentAlertDialog } from "@/features/form-details/comments/components/delete-comment-alert-dialog";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";

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

  const user = session.data?.user;
  const isUserAuthor = user?.name === authorName;
  const isUserAdmin = user?.roles.includes(Role.ADMIN);

  if (!isUserAuthor && !isUserAdmin) return null;

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
          <DeleteCommentAlertDialog
            formIdOrSlug={formIdOrSlug}
            commentId={commentId}
          />
        </DropdownMenuContent>
      </DropdownMenu>
    </span>
  );
};
