import { Card } from "@/core/components/ui/card";
import { Skeleton } from "@/core/components/ui/skeleton";

const COMMENT_SKELETON_COUNT = 3;

export const CommentSkeleton = () => (
  <Card className="flex flex-col gap-3 p-4">
    {/* Author */}
    <div className="flex items-center gap-2">
      <Skeleton className="h-10 w-10 rounded-full" />
      <Skeleton className="h-5 w-32" />
    </div>
    {/* Content */}
    <div className="flex flex-col gap-1.5">
      <Skeleton className="h-3.5 w-full" />
      <Skeleton className="h-3.5 w-4/5" />
    </div>
    {/* Rating + date */}
    <div className="flex items-center justify-between">
      <Skeleton className="h-6 w-20" />
      <Skeleton className="h-3 w-28" />
    </div>
  </Card>
);

export const FormDetailsLoading = () => {
  return (
    <section className="px-5 py-10 lg:px-30">
      {/* Banner - mobile */}
      <Skeleton className="h-64 w-full rounded-b-none md:hidden" />
      {/* Banner - desktop */}
      <Skeleton className="hidden h-110 w-full rounded-b-none md:block" />

      {/* Details card */}
      <Card className="flex min-h-50 w-full flex-col justify-between gap-4 rounded-t-none p-4">
        {/* Header: title + rating */}
        <div className="flex flex-wrap items-center gap-2">
          <Skeleton className="h-8 w-64" />
          <Skeleton className="ml-auto h-5 w-28" />
        </div>

        {/* Description */}
        <div className="flex flex-col gap-1.5">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-3/5" />
        </div>

        {/* Tags row */}
        <div className="flex flex-wrap items-center gap-4">
          <Skeleton className="h-4 w-28" />
          <Skeleton className="h-4 w-28" />
          <Skeleton className="h-4 w-28" />
          <Skeleton className="ml-auto h-4 w-40" />
        </div>
      </Card>

      {/* Question list toggle button */}
      <div className="mt-2">
        <Skeleton className="h-8 w-36" />
      </div>

      {/* Comments section */}
      <div className="flex flex-col gap-2 pt-4">
        {/* Create comment input */}
        <Skeleton className="h-10 w-full" />

        {Array.from({ length: COMMENT_SKELETON_COUNT }).map((_, i) => (
          <CommentSkeleton key={`comment-skeleton-${i}`} />
        ))}
      </div>
    </section>
  );
};
