import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";
import { StarButton } from "./components/star-button";
import { useCreateFormRating } from "./hooks/use-create-form-rating";
import { useDeleteFormRating } from "./hooks/use-delete-form-rating";

interface FormRatingProps {
  readonly formIdOrSlug: string;
  readonly userRating: number | null;
}

export const FormRating = ({ formIdOrSlug, userRating }: FormRatingProps) => {
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
    <section className="flex gap-3">
      <div className="flex">
        {[0, 1, 2, 3, 4].map((index) => {
          const fillFraction =
            hoveredIndex !== null
              ? 0
              : Math.round(
                  Math.min(Math.max((userRating ?? 0) - index, 0), 1) * 10,
                ) / 10;
          const isHovered = hoveredIndex !== null && index <= hoveredIndex;

          return (
            <StarButton
              key={index}
              fillFraction={fillFraction}
              isHovered={isHovered}
              onMouseEnter={() => handleStarHover(index)}
              onMouseLeave={handleMouseLeave}
              onClick={() => handleStarClick(index)}
              disabled={!session || isPending}
            />
          );
        })}
      </div>
      <p>Rating: {userRating ?? 0}</p>
    </section>
  );
};
