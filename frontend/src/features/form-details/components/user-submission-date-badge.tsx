import { Badge } from "@/core/components/ui/badge";
import { useFetchMySubmission } from "@/features/form-details/my-submission/hooks/use-fetch-my-submission";
import { useFormatter, useTranslations } from "next-intl";

interface UserSubmissionDateBadgeProps {
  readonly formIdOrSlug: string;
}

export const UserSubmissionDateBadge = ({
  formIdOrSlug,
}: UserSubmissionDateBadgeProps) => {
  const t = useTranslations("formDetailsPage.banner");
  const format = useFormatter();

  const { data: mySubmission } = useFetchMySubmission(formIdOrSlug);

  if (!mySubmission) return null;

  return (
    <Badge className="absolute top-4 right-4">
      {t("formFinished", {
        finishedAt: format.dateTime(new Date(mySubmission.createdAt), "long"),
      })}
    </Badge>
  );
};
