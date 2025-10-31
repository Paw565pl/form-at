import { Card } from "@/core/components/ui/card";
import { forms } from "@/features/public-forms-list-view/forms";
import { useFormatter, useTranslations } from "next-intl";
import Link from "next/link";

export const ListView = () => {
  const t = useTranslations("publicFormsList");
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
                {t("by")} {form.author.name}
              </span>
            </header>
            <p className="text-sm lg:mr-36">{form.description}</p>
            <footer className="text-muted-foreground mt-1 flex flex-wrap justify-between text-sm">
              <span>
                {t("questions", { count: form.questionsCount })} •{" "}
                {t("submissions", { count: form.submissionsCount })} •{" "}
                {form.estimatedDuration} min
              </span>
              <span className="text-muted-foreground text-sm">
                {format.dateTime(new Date(form.createdAt), {
                  year: "numeric",
                  month: "long",
                  day: "numeric",
                })}
              </span>
            </footer>
          </Card>
        </Link>
      ))}
    </div>
  );
};
