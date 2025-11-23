import { Card } from "@/core/components/ui/card";
import { placeholder_image_url } from "@/features/form-list/example-forms";
import { useFetchFormPage } from "@/features/form-list/hooks/use-fetch-form-page";
import { formFilterSearchParams } from "@/features/form-list/search-params/form-search-params";
import { useFormatter, useTranslations } from "next-intl";
import Image from "next/image";
import Link from "next/link";
import { useQueryStates } from "nuqs";
import InfiniteScroll from "react-infinite-scroll-component";

export const GridView = () => {
  const t = useTranslations("formListPage");
  const format = useFormatter();
  const [filters] = useQueryStates(formFilterSearchParams);
  const {
    data: formPages,
    isLoading,
    isError,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useFetchFormPage(filters);

  if (formPages?.pages.at(0)?.content.length == 0)
    return (
      <p className="p-4 text-center">No forms found for given criteria.</p>
    );

  if (isError)
    return (
      <p className="p-4 text-center">
        An unexpected error occurred. Please try again later.
      </p>
    );

  const dataLength =
    formPages?.pages.reduce((acc, curr) => acc + curr.content.length, 0) || 0;

  return (
    <>
      <InfiniteScroll
        dataLength={dataLength}
        next={fetchNextPage}
        hasMore={hasNextPage}
        loader={null}
        className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3"
      >
        {formPages?.pages.map((page) =>
          page.content.map((form) => (
            <Link href={`/forms/${form.slug}`} key={form.id}>
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
          )),
        )}
      </InfiniteScroll>

      {(isLoading || isFetchingNextPage) && <p>{t("loading")}</p>}

      {!isLoading && !hasNextPage && (
        <p className="text-muted-foreground p-4 text-center text-sm">
          {t("end")}
        </p>
      )}
    </>
  );
};
