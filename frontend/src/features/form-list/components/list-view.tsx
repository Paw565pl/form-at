import { Card } from "@/core/components/ui/card";
import { formatDuration } from "@/core/utils/formatDuration";
import { forms } from "@/features/form-list/example-forms";
import { useFormatter, useTranslations } from "next-intl";
import Link from "next/link";

export const ListView = () => {
  const t = useTranslations("formListPage");
  const format = useFormatter();

  return (
    <div className="flex flex-col gap-2">
      {forms.map((form) => (
        <Link href={{ pathname: `/forms/${form.id}` }} key={form.id}>
          <Card
            key={form.id}
            className="hover:border-primary gap-1 p-3 transition-all"
          >
            <header className="flex flex-wrap items-center justify-between gap-1">
              <h1 className="font-medium">{form.name}</h1>
              <span className="text-muted-foreground text-sm">
                {form.authorName
                  ? `${t("by", { name: form.authorName })}`
                  : t("byUnknown")}
              </span>
            </header>
            <p className="text-sm lg:mr-36">{form.description}</p>
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
      ))}
    </div>
  );
};
