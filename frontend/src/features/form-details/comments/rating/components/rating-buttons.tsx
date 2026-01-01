import { Button } from "@/core/components/ui/button";
import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";
import { useSession } from "next-auth/react";
import { useCreateCommentRating } from "../hooks/use-create-comment-rating";
import { useDeleteCommentRating } from "../hooks/use-delete-comment-rating";

interface RatingButtonsProps {
  readonly formIdOrSlug: string;
  readonly commentId: string;
  readonly ratingScore: number | null;
  readonly userRating: "UPVOTE" | "DOWNVOTE" | null;
}

export const RatingButtons = ({
  formIdOrSlug,
  commentId,
  ratingScore,
  userRating,
}: RatingButtonsProps) => {
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
    type: "UPVOTE" | "DOWNVOTE",
    userRating: "UPVOTE" | "DOWNVOTE" | null,
  ) => {
    if (!session || isPending) return;
    if (userRating === type) {
      deleteRating();
      return;
    }

    createRating({ type });
  };

  return (
    <section className="flex items-center gap-0.5">
      {ratingScore ? (
        <span className="text-muted-foreground text-sm font-medium">
          {ratingScore}
        </span>
      ) : null}

      <Button
        variant="ghost"
        size="icon-sm"
        onClick={() => handleRating("UPVOTE", userRating)}
        disabled={!session || isPending || isDeleting}
      >
        <ICONS.like
          className={cn({
            "text-primary": userRating === "UPVOTE",
          })}
        />
      </Button>
      <Button
        variant="ghost"
        size="icon-sm"
        onClick={() => handleRating("DOWNVOTE", userRating)}
        disabled={!session || isPending || isDeleting}
      >
        <ICONS.dislike
          className={cn({
            "text-primary": userRating === "DOWNVOTE",
          })}
        />
      </Button>
    </section>
  );
};
