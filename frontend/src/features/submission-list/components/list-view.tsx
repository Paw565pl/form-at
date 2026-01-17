import { Card } from "@/core/components/ui/card";
import { useTranslations } from "next-intl";
import InfiniteScroll from "react-infinite-scroll-component";
import { useFetchSubmissionPages } from "../hooks/use-fetch-submission-pages";

interface ListViewProps {
  readonly formIdOrSlug: string;
}

export const ListView = ({ formIdOrSlug }: ListViewProps) => {
  const t = useTranslations("formListPage");
  const {
    data: submissionPages,
    isLoading,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useFetchSubmissionPages(formIdOrSlug);

  if (submissionPages?.pages.at(0)?.content.length == 0)
    return <p className="p-4 text-center">{t("empty")}</p>;

  const dataLength =
    submissionPages?.pages.reduce(
      (acc, curr) => acc + curr.content.length,
      0,
    ) || 0;

  return (
    <>
      <InfiniteScroll
        dataLength={dataLength}
        next={fetchNextPage}
        hasMore={hasNextPage}
        loader={null}
        className="flex flex-col gap-2"
      >
        {submissionPages?.pages.map((page) =>
          page.content.map((submission) => (
            <Card key={submission.id} className="flex-row justify-between p-4">
              <p className="font-semibold">authorname</p>
              <span className="text-muted-foreground">
                {submission.createdAt}
              </span>
            </Card>
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
