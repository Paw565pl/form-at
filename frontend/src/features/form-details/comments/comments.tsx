import { Card } from "@/core/components/ui/card";
import { UserImage } from "@/core/components/user-image/user-image";
import { CommentOptions } from "@/features/form-details/comments/components/comment-options";
import { CreateCommentForm } from "@/features/form-details/comments/components/create-comment-form";
import { EditCommentForm } from "@/features/form-details/comments/components/edit-comment-form";
import { useFetchFormCommentsPages } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { RatingButtons } from "@/features/form-details/comments/rating/components/rating-buttons";
import { useFormatter, useTranslations } from "next-intl";
import Link from "next/link";
import { useState } from "react";
import InfiniteScroll from "react-infinite-scroll-component";

interface CommentsProps {
  readonly formIdOrSlug: string;
}

export const Comments = ({ formIdOrSlug }: CommentsProps) => {
  const t = useTranslations("formDetailsPage.comments");
  const format = useFormatter();
  const [editedCommentId, setEditedCommentId] = useState<string | null>(null);

  const {
    data: formCommentsPages,
    isLoading,
    error,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useFetchFormCommentsPages(formIdOrSlug);

  const dataLength =
    formCommentsPages?.pages.reduce(
      (acc, curr) => acc + curr.content.length,
      0,
    ) || 0;

  if (error) {
    return (
      <div className="flex flex-col gap-2 pt-4">
        <CreateCommentForm formIdOrSlug={formIdOrSlug} />
        <p className="text-error text-center">{t("loadingError")}</p>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="flex flex-col gap-2 pt-4">
        <CreateCommentForm formIdOrSlug={formIdOrSlug} />
        <p className="text-muted-foreground text-center">{t("loading")}</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-2 pt-4">
      <CreateCommentForm formIdOrSlug={formIdOrSlug} />

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
              {comment.authorName ? (
                <Link href={`/users/${comment.authorName}`} className="w-fit">
                  <header className="flex items-center gap-2">
                    <UserImage className="h-10 w-10" />
                    <h1 className="text-xl font-bold hover:underline">
                      {comment.authorName}
                    </h1>
                  </header>
                </Link>
              ) : (
                <header className="flex items-center gap-2">
                  <UserImage className="h-10 w-10" />
                  <h1 className="text-xl font-bold">{t("deletedUser")}</h1>
                </header>
              )}

              {editedCommentId === comment.id ? (
                <EditCommentForm
                  formIdOrSlug={formIdOrSlug}
                  commentId={comment.id}
                  initialContent={comment.content}
                  onSuccess={() => setEditedCommentId(null)}
                  onCancel={() => setEditedCommentId(null)}
                />
              ) : (
                <>
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
                    onEdit={() => setEditedCommentId(comment.id)}
                  />

                  <span className="text-muted-foreground absolute right-6 bottom-6 text-xs">
                    {format.dateTime(new Date(comment.createdAt), "long")}
                  </span>
                </>
              )}
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
