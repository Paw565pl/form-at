import { Button } from "@/core/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/core/components/ui/dropdown-menu";
import { ICONS } from "@/core/config/icons";
import { useDeleteComment } from "@/features/form-details/comments/hooks/use-delete-comment";
import { useTranslations } from "next-intl";

interface CommentOptionsProps {
  readonly formIdOrSlug: string;
  readonly commentId: string;
}

export const CommentOptions = ({
  formIdOrSlug,
  commentId,
}: CommentOptionsProps) => {
  const t = useTranslations("formDetailsPage.comments");
  const { mutate: deleteComment, isPending } = useDeleteComment({
    formIdOrSlug,
    commentId,
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
          <DropdownMenuItem>
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
