import { Card } from "@/core/components/ui/card";
import {
  forms,
  placeholder_image_url,
} from "@/features/public-forms-list-view/forms";
import { useFormatter, useTranslations } from "next-intl";
import Link from "next/link";

export const GridView = () => {
  const t = useTranslations("publicFormsList");
  const format = useFormatter();

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {forms.map((form) => (
        <Link href={{ pathname: `/forms/${form.id}` }} key={form.id}>
          <Card
            key={form.id}
            className="group hover:border-primary h-full overflow-hidden transition-all"
          >
            <div
              style={{
                backgroundImage: `url(${form.thumbnailKey || placeholder_image_url})`,
                backgroundSize: "cover",
                backgroundPosition: "center",
              }}
              className="min-h-36 origin-bottom rounded-t-md transition-transform group-hover:scale-105"
            ></div>
            <div className="flex h-full flex-col gap-2 p-3">
              <header className="bg-card flex flex-wrap items-center justify-between">
                <h1 className="font-medium">{form.name}</h1>
                <span className="text-muted-foreground text-sm">
                  {t("by")} {form.author.name}
                </span>
              </header>
              <p className="line-clamp-4 overflow-hidden text-sm">
                {form.description}
              </p>
              <footer className="text-muted-foreground mt-auto flex flex-wrap justify-between gap-1 text-sm">
                <span>
                  {t("questions", { count: form.questionsCount })} •{" "}
                  {t("submissions", { count: form.submissionsCount })}
                </span>
                <span className="text-muted-foreground text-sm">
                  {format.dateTime(new Date(form.createdAt), {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                  })}
                </span>
              </footer>
            </div>
          </Card>
        </Link>
      ))}
    </div>
  );
};
