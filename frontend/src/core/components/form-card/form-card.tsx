import { Card } from "@/core/components/ui/card";
import { FormListResponseDto } from "@/core/types/form";
import { formatDuration } from "@/core/utils/formatDuration";
import { useFormatter, useTranslations } from "next-intl";
import Link from "next/link";

interface FormCardProps {
  form: FormListResponseDto;
  showAuthor?: boolean;
}

export const FormCard = ({ form, showAuthor = false }: FormCardProps) => {
  const t = useTranslations("formListPage");
  const format = useFormatter();

  return (
    <Link href={{ pathname: `/forms/${form.id}` }} key={form.id}>
      <Card
        key={form.id}
        className="hover:border-primary gap-1 p-3 transition-all"
      >
        <header
          className={
            showAuthor
              ? "flex flex-wrap items-center justify-between gap-1"
              : "font-semibold"
          }
        >
          {showAuthor ? (
            <>
              <h1 className="font-medium">{form.name}</h1>
              <span className="text-muted-foreground text-sm">
                {form.authorName
                  ? `${t("by", { name: form.authorName })}`
                  : t("byUnknown")}
              </span>
            </>
          ) : (
            form.name
          )}
        </header>
        <p className={showAuthor ? "text-sm lg:mr-36" : "text-sm"}>
          {form.description}
        </p>
        <footer className="text-muted-foreground mt-1 flex flex-wrap justify-between text-sm">
          <span>
            {t("questions", { count: form.questionsCount })} •{" "}
            {t("submissions", { count: form.submissionsCount })} •{" "}
            {formatDuration(form.estimatedDuration)}
          </span>
          <span className="text-muted-foreground text-sm">
            {format.dateTime(new Date(form.createdAt), "long")}
          </span>
        </footer>
      </Card>
    </Link>
  );
};
