import { Card } from "@/core/components/ui/card";
import {
  forms,
  placeholder_image_url,
} from "@/features/form-list/example-forms";
import { useFormatter, useTranslations } from "next-intl";
import Image from "next/image";
import Link from "next/link";

export const GridView = () => {
  const t = useTranslations("formListPage");
  const format = useFormatter();

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {forms.map((form) => (
        // TODO: fix when details page merges in
        <Link href={{ pathname: `/forms/${form.id}` }} key={form.id}>
          <Card
            key={form.id}
            className="group hover:border-primary h-full overflow-hidden transition-all"
          >
            <div className="relative min-h-40">
              <Image
                src={form.thumbnail || placeholder_image_url}
                alt={form.name}
                className="origin-bottom object-cover transition-transform group-hover:scale-105"
                fill
              />
            </div>

            <div className="flex h-full flex-col gap-2 p-3">
              <header className="bg-card flex flex-wrap items-center justify-between">
                <h1 className="font-medium">{form.name}</h1>
                <span className="text-muted-foreground text-sm">
                  {form.authorName
                    ? `${t("by", { name: form.authorName })}`
                    : t("byUnknown")}
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
                  {format.dateTime(new Date(form.createdAt), "long")}
                </span>
              </footer>
            </div>
          </Card>
        </Link>
      ))}
    </div>
  );
};
