import { StarButton } from "@/features/form-details/rating/components/star-button";
import { useCreateFormRating } from "@/features/form-details/rating/hooks/use-create-form-rating";
import { useDeleteFormRating } from "@/features/form-details/rating/hooks/use-delete-form-rating";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

interface FormRatingProps {
  readonly formIdOrSlug: string;
  readonly ratingsCount: number;
  readonly userRating: number | null;
  readonly ratingAvg: number;
}

export const FormRating = ({
  formIdOrSlug,
  ratingsCount,
  userRating,
  ratingAvg,
}: FormRatingProps) => {
  const t = useTranslations("formDetailsPage.rating");
  const { data: session } = useSession();
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

  const { mutate: createRating, isPending: isCreating } =
    useCreateFormRating(formIdOrSlug);
  const { mutate: deleteRating, isPending: isDeleting } =
    useDeleteFormRating(formIdOrSlug);

  const isPending = isCreating || isDeleting;

  const handleStarClick = (index: number) => {
    if (!session || isPending) return;

    const newRating = index + 1;

    if (userRating === newRating) {
      deleteRating(undefined, {
        onSuccess: () => {
          toast.success(t("ratingDeleted"));
        },
        onError: () => {
          toast.error(t("ratingError"));
        },
      });
    } else {
      createRating(
        { ratingValue: newRating },
        {
          onError: () => {
            toast.error(t("ratingError"));
          },
        },
      );
    }
  };

  const handleStarHover = (index: number) => {
    setHoveredIndex(index);
  };

  const handleMouseLeave = () => {
    setHoveredIndex(null);
  };

  return (
    <section className="flex items-center gap-3">
      <header className="text-4xl">{ratingAvg.toFixed(1)}</header>

      <div className="flex flex-col items-center gap-1">
        <div className="flex">
          {[0, 1, 2, 3, 4].map((index) => {
            let fillFraction = 0;
            if (hoveredIndex === null) {
              // If user has rated, show their rating; otherwise show average
              if (userRating !== null) {
                fillFraction = index < userRating ? 1 : 0;
              } else {
                fillFraction =
                  Math.round(Math.min(Math.max(ratingAvg - index, 0), 1) * 10) /
                  10;
              }
            }
            const isHovered = hoveredIndex !== null && index <= hoveredIndex;

            return (
              <StarButton
                key={index}
                fillFraction={fillFraction}
                userRating={userRating}
                isHovered={isHovered}
                onMouseEnter={() => handleStarHover(index)}
                onMouseLeave={handleMouseLeave}
                onClick={() => handleStarClick(index)}
                disabled={!session || isPending}
              />
            );
          })}
        </div>
        <p className="text-muted-foreground text-sm">
          {t("ratingsCount", { count: ratingsCount })}
        </p>
      </div>
    </section>
  );
};
