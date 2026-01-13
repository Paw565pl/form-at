import { StarSet } from "@/features/form-details/rating/components/star-set";
import { useTranslations } from "next-intl";

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

  return (
    <section className="ml-auto flex items-center gap-3">
      <div className="flex flex-col items-center gap-1">
        <StarSet
          formIdOrSlug={formIdOrSlug}
          userRating={userRating}
          ratingAvg={ratingAvg}
        />
        <p className="text-muted-foreground text-sm">
          {ratingAvg.toFixed(1)} ({t("ratingsCount", { count: ratingsCount })})
        </p>
      </div>
    </section>
  );
};
