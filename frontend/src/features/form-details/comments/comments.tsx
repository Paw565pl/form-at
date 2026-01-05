import { Card } from "@/core/components/ui/card";
import { UserImage } from "@/core/components/user-image/user-image";
import { AddComments } from "@/features/form-details/comments/components/add-comments";
import { CommentOptions } from "@/features/form-details/comments/components/comment-options";
import { useFetchFormCommentsPages } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { RatingButtons } from "@/features/form-details/comments/rating/components/rating-buttons";
import { useFormatter, useTranslations } from "next-intl";
import InfiniteScroll from "react-infinite-scroll-component";

interface FormCommentsProps {
  readonly formIdOrSlug: string;
}

export const Comments = ({ formIdOrSlug }: FormCommentsProps) => {
  const t = useTranslations("formDetailsPage.comments");
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

  if (isLoading) {
    return (
      <div className="flex flex-col gap-2 pt-4">
        <AddComments formIdOrSlug={formIdOrSlug} />
        <p className="text-muted-foreground text-center">{t("loading")}</p>
      </div>
    );
  }

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

              <CommentOptions
                formIdOrSlug={formIdOrSlug}
                commentId={comment.id}
                authorName={comment.authorName}
              />

              <span className="text-muted-foreground absolute right-6 bottom-6 text-xs">
                {format.dateTime(new Date(comment.createdAt), "long")}
              </span>
            </Card>
          )),
        )}
      </InfiniteScroll>

      {isFetchingNextPage && (
        <p className="text-muted-foreground text-center">{t("loading")}</p>
      )}
    </div>
  );
};
