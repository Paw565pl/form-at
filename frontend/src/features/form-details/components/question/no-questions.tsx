import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/core/components/ui/empty";
import { MessageSquareX } from "lucide-react";
import { useTranslations } from "next-intl";

export const NoQuestions = () => {
  const t = useTranslations("publicFormView.questionList");

  return (
    <Empty className="border-muted-foreground w-full border border-dashed">
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <MessageSquareX />
        </EmptyMedia>
        <EmptyTitle>{t("noQuestions")}</EmptyTitle>
        <EmptyDescription>{t("noQuestionsDescription")}</EmptyDescription>
      </EmptyHeader>
    </Empty>
  );
};
