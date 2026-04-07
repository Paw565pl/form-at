import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { ICONS } from "@/core/config/icons";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";

interface ThanksMessageCardProps {
  readonly thanksMessage: string | null;
  readonly slug: string;
}

export const ThanksMessageCard = ({
  thanksMessage,
  slug,
}: ThanksMessageCardProps) => {
  const router = useRouter();
  const t = useTranslations("submissionCreatePage");

  return (
    <Card className="flex flex-col items-center justify-center gap-5 p-6">
      <div className="bg-primary/10 flex h-16 w-16 items-center justify-center rounded-full">
        <ICONS.check className="text-primary h-10 w-10" />
      </div>
      <div className="flex flex-col items-center gap-2">
        <h2 className="mb-2 text-xl font-medium">{t("submissionCreated")}</h2>
        <p>{thanksMessage || t("defaultThanksMessage")}</p>
      </div>
      <Button
        size="sm"
        variant="default"
        onClick={() => router.replace(`/forms/${slug}`)}
      >
        {t("backToDetails")}
      </Button>
    </Card>
  );
};
