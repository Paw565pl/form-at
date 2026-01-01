import { Card } from "@/core/components/ui/card";
import { UserImage } from "@/core/components/user-image/user-image";
import { useFormatter } from "next-intl";
import InfiniteScroll from "react-infinite-scroll-component";
import { AddComments } from "./components/add-comments";
import { useFetchFormCommentsPages } from "./hooks/use-fetch-form-comments-pages";
import { RatingButtons } from "./rating/components/rating-buttons";

interface FormCommentsProps {
  readonly formIdOrSlug: string;
}

export const Comments = ({ formIdOrSlug }: FormCommentsProps) => {
  const format = useFormatter();

  const {
    data: formCommentsPages,
    isLoading,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useFetchFormCommentsPages(formIdOrSlug);

  const dataLength =
    formCommentsPages?.pages.reduce(
      (acc, curr) => acc + curr.content.length,
      0,
    ) || 0;

  return (
    <div className="flex flex-col gap-2 pt-4">
      <AddComments formIdOrSlug={formIdOrSlug} />

      <InfiniteScroll
        dataLength={dataLength}
        next={fetchNextPage}
        hasMore={hasNextPage}
        loader={null}
        className="flex flex-col gap-2"
      >
        {formCommentsPages?.pages.map((page) =>
          page.content.map((comment) => (
            <Card key={comment.id} className="relative flex flex-col p-4">
              <header className="flex items-center gap-3">
                <UserImage className="h-10 w-10" />
                <h1 className="text-xl font-bold">{comment.authorName}</h1>
              </header>

              <p className="py-2 text-sm">{comment.content}</p>

              <RatingButtons
                ratingScore={comment.ratingScore}
                userRating={comment.userRating}
                formIdOrSlug={formIdOrSlug}
                commentId={comment.id}
              />

              <span className="text-muted-foreground absolute right-6 bottom-6 text-xs">
                {format.dateTime(new Date(comment.createdAt), "long")}
              </span>
            </Card>
          )),
        )}
      </InfiniteScroll>
    </div>
  );
};
