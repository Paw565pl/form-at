import { FormCard } from "@/core/components/form-card/form-card";
import { useFetchFormPages } from "@/features/form-list/hooks/use-fetch-form-pages";
import {
  formFilterSearchParams,
  formSortSearchParams,
} from "@/features/form-list/search-params/form-search-params";
import { useTranslations } from "next-intl";
import { useQueryStates } from "nuqs";
import InfiniteScroll from "react-infinite-scroll-component";

export const ListView = () => {
  const t = useTranslations("formListPage");
  const [filtersDto] = useQueryStates(formFilterSearchParams);
  const [sortDto] = useQueryStates(formSortSearchParams);
  const {
    data: formPages,
    isLoading,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useFetchFormPages(filtersDto, sortDto);

  if (formPages?.pages.at(0)?.content.length == 0)
    return <p className="p-4 text-center">{t("empty")}</p>;

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
            <FormCard form={form} key={form.id} showAuthor />
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
