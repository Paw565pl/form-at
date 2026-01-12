import { useTranslations } from "next-intl";
import { StarSet } from "./components/star-set";

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
    <section className="flex items-center gap-3">
      <header className="text-4xl">{ratingAvg.toFixed(1)}</header>

      <div className="flex flex-col items-center gap-1">
        <StarSet
          formIdOrSlug={formIdOrSlug}
          userRating={userRating}
          ratingAvg={ratingAvg}
        />
        <p className="text-muted-foreground text-sm">
          {t("ratingsCount", { count: ratingsCount })}
        </p>
      </div>
    </section>
  );
};
