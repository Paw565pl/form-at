import { getStarFillFraction } from "@/core/utils/get-star-fill-fraction";
import { StarButton } from "@/features/form-details/rating/components/star-button";
import { useCreateFormRating } from "@/features/form-details/rating/hooks/use-create-form-rating";
import { useDeleteFormRating } from "@/features/form-details/rating/hooks/use-delete-form-rating";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

interface StarSetProps {
  readonly formIdOrSlug: string;
  readonly userRating: number | null;
  readonly ratingAvg: number;
}

export const StarSet = ({
  formIdOrSlug,
  userRating,
  ratingAvg,
}: StarSetProps) => {
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

  return (
    <div className="flex">
      {[0, 1, 2, 3, 4].map((index) => {
        const fillFraction = getStarFillFraction(
          index,
          hoveredIndex,
          userRating,
          ratingAvg,
        );
        const isHovered = hoveredIndex !== null && index <= hoveredIndex;

        return (
          <StarButton
            key={index}
            fillFraction={fillFraction}
            userRating={userRating}
            isHovered={isHovered}
            onMouseEnter={() => setHoveredIndex(index)}
            onMouseLeave={() => setHoveredIndex(null)}
            onClick={() => handleStarClick(index)}
            disabled={!session || isPending}
            ariaLabel={t("starAriaLabel", { starNumber: index + 1 })}
          />
        );
      })}
    </div>
  );
};
