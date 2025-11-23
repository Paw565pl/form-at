import { Card } from "@/core/components/ui/card";
import { formatDuration } from "@/core/utils/formatDuration";
import { useFetchFormPage } from "@/features/form-list/hooks/use-fetch-form-page";
import {
  formFilterSearchParams,
  formSortSearchParams,
} from "@/features/form-list/search-params/form-search-params";
import { useFormatter, useTranslations } from "next-intl";
import Link from "next/link";
import { useQueryStates } from "nuqs";
import InfiniteScroll from "react-infinite-scroll-component";

export const ListView = () => {
  const t = useTranslations("formListPage");
  const format = useFormatter();
  const [filtersDto] = useQueryStates(formFilterSearchParams);
  const [sortDto] = useQueryStates(formSortSearchParams);
  const {
    data: formPages,
    isLoading,
    isError,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useFetchFormPage(filtersDto, sortDto);

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
        className="flex flex-col gap-2"
      >
        {formPages?.pages.map((page) =>
          page.content.map((form) => (
            <Link href={`/forms/${form.slug}`} key={form.id}>
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
