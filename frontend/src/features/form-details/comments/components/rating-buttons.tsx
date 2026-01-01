import { Button } from "@/core/components/ui/button";
import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";

interface RatingButtonsProps {
  readonly ratingScore: number | null;
  readonly userRating: "UPVOTE" | "DOWNVOTE" | null;
}

export const RatingButtons = ({
  ratingScore,
  userRating,
}: RatingButtonsProps) => {
  return (
    <section className="flex items-center gap-0.5">
      {ratingScore ? (
        <span className="text-muted-foreground text-sm font-medium">
          {ratingScore}
        </span>
      ) : null}

      <Button variant="ghost" size="icon-sm">
        <ICONS.like
          className={cn({
            "text-primary": userRating === "UPVOTE",
          })}
        />
      </Button>
      <Button variant="ghost" size="icon-sm">
        <ICONS.dislike
          className={cn({
            "text-primary": userRating === "DOWNVOTE",
          })}
        />
      </Button>
    </section>
  );
};
