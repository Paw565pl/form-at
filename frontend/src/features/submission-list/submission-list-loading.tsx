import { Card } from "@/core/components/ui/card";
import { Skeleton } from "@/core/components/ui/skeleton";

const SUBMISSION_CARD_SKELETON_COUNT = 8;

const SubmissionCardSkeleton = () => (
  <Card className="flex-col-reverse justify-between gap-2 p-4 sm:flex-row">
    <Skeleton className="h-5 w-40" />
    <Skeleton className="h-4 w-48" />
  </Card>
);

export const SubmissionListLoading = () => {
  return (
    <section className="flex w-full flex-col gap-2 px-5 py-10 lg:px-30">
      {/* Header */}
      <header className="mb-2 flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center">
          <Skeleton className="h-8 w-8 rounded-md" />
          <Skeleton className="ml-4 h-6 w-60" />
        </div>
        <Skeleton className="ml-3 h-8 w-36 rounded-md" />
      </header>

      {/* Submission cards */}
      <div className="flex flex-col gap-2">
        {Array.from({ length: SUBMISSION_CARD_SKELETON_COUNT }).map((_, i) => (
          <SubmissionCardSkeleton key={`submission-skeleton-${i}`} />
        ))}
      </div>
    </section>
  );
};
