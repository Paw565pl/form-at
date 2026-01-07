import { Button } from "@/core/components/ui/button";
import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";
import { CommentRatingType } from "@/core/types/comment";
import { useCreateCommentRating } from "@/features/form-details/comments/rating/hooks/use-create-comment-rating";
import { useDeleteCommentRating } from "@/features/form-details/comments/rating/hooks/use-delete-comment-rating";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { toast } from "sonner";

interface RatingButtonsProps {
  readonly formIdOrSlug: string;
  readonly commentId: string;
  readonly ratingScore: number;
  readonly userRating: CommentRatingType | null;
}

export const RatingButtons = ({
  formIdOrSlug,
  commentId,
  ratingScore,
  userRating,
}: RatingButtonsProps) => {
  const t = useTranslations("formDetailsPage.comments");
  const { data: session } = useSession();
  const { mutate: createRating, isPending } = useCreateCommentRating({
    formIdOrSlug,
    commentId,
  });
  const { mutate: deleteRating, isPending: isDeleting } =
    useDeleteCommentRating({
      formIdOrSlug,
      commentId,
    });

  const handleRating = (
    type: CommentRatingType,
    userRating: CommentRatingType | null,
  ) => {
    if (!session || isPending) return;
    if (userRating === type) {
      deleteRating();
    } else {
      createRating(
        { type },
        {
          onError: () => {
            toast.error(t("ratingError"));
          },
        },
      );
    }
  };

  return (
    <section className="flex items-center gap-0.5">
      <Button
        variant="ghost"
        size="icon-sm"
        aria-label="upvote"
        onClick={() => handleRating(CommentRatingType.upvote, userRating)}
        disabled={!session || isPending || isDeleting}
      >
        <ICONS.like
          className={cn({
            "text-primary": userRating === CommentRatingType.upvote,
          })}
        />
      </Button>
      <span className="text-muted-foreground text-sm font-medium">
        {ratingScore || 0}
      </span>
      <Button
        variant="ghost"
        size="icon-sm"
        aria-label="downvote"
        onClick={() => handleRating(CommentRatingType.downvote, userRating)}
        disabled={!session || isPending || isDeleting}
      >
        <ICONS.dislike
          className={cn({
            "text-primary": userRating === CommentRatingType.downvote,
          })}
        />
      </Button>
    </section>
  );
};
