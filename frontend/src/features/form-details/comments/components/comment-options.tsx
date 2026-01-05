import { Button } from "@/core/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/core/components/ui/dropdown-menu";
import { ICONS } from "@/core/config/icons";
import { Role } from "@/features/auth/types/role";
import { useDeleteComment } from "@/features/form-details/comments/hooks/use-delete-comment";
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
  const { mutate: deleteComment, isPending } = useDeleteComment({
    formIdOrSlug,
    commentId,
  });

  const user = session.data?.user;

  //   TODO: Add proper owner check
  if (user?.name !== authorName && user?.roles.includes(Role.ADMIN) === false) {
    return null;
  }

  return (
    <span className="absolute top-4 right-4">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button aria-label={t("moreOptions")} size="icon-sm" variant="ghost">
            <ICONS.more />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem onClick={onEdit}>
            <ICONS.edit />
            {t("edit")}
          </DropdownMenuItem>
          <DropdownMenuItem
            onClick={() => deleteComment()}
            disabled={isPending}
            variant="destructive"
          >
            <ICONS.delete />
            {t("delete")}
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </span>
  );
};
