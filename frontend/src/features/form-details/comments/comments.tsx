import { Button } from "@/core/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from "@/core/components/ui/card";
import { Input } from "@/core/components/ui/input";
import { ICONS } from "@/core/config/icons";
import InfiniteScroll from "react-infinite-scroll-component";
import { useFetchFormCommentsPages } from "./hooks/use-fetch-form-comments-pages";

interface FormCommentsProps {
  readonly formIdOrSlug: string;
}

export const Comments = ({ formIdOrSlug }: FormCommentsProps) => {
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
      <div className="flex items-center gap-2">
        <Input placeholder="Add a comment..." className="w-full" />
        <Button aria-label="send" variant="outline" size="icon-sm">
          <ICONS.send />
        </Button>
      </div>
      <InfiniteScroll
        dataLength={dataLength}
        next={fetchNextPage}
        hasMore={hasNextPage}
        loader={null}
        className="flex flex-col gap-2"
      >
        {formCommentsPages?.pages.map((page) =>
          page.content.map((comment) => (
            <Card key={comment.id} className="py-4">
              <CardHeader>{comment.authorName}</CardHeader>
              <CardContent className="text-sm">{comment.content}</CardContent>
              <CardFooter className="text-muted-foreground text-xs">
                {comment.createdAt}
              </CardFooter>
            </Card>
          )),
        )}
      </InfiniteScroll>
    </div>
  );
};
