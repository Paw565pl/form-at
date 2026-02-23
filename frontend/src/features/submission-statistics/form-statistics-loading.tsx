import { Card } from "@/core/components/ui/card";
import { Skeleton } from "@/core/components/ui/skeleton";

const QUESTION_SKELETON_COUNT = 3;

const StatisticsQuestionSkeleton = () => (
  <Card className="gap-2 p-4">
    {/* Question header */}
    <div className="flex flex-col flex-wrap gap-1 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex gap-1">
        <Skeleton className="h-5 w-6" />
        <Skeleton className="h-5 w-48" />
      </div>
      <Skeleton className="ml-3 h-4 w-24" />
    </div>

    {/* Answer progress bars */}
    <div className="flex flex-col gap-2">
      {Array.from({ length: 3 }).map((_, i) => (
        <div
          className="flex w-full max-w-sm flex-col gap-1.5"
          key={`progress-${i}`}
        >
          <div className="flex items-end justify-between">
            <Skeleton className="h-3 w-24" />
            <Skeleton className="h-3 w-16" />
          </div>
          <Skeleton className="h-2 w-full rounded-full" />
        </div>
      ))}
    </div>
  </Card>
);

export const FormStatisticsLoading = () => {
  return (
    <section className="flex w-full flex-col gap-2 px-5 py-10 lg:px-30">
      {/* Header */}
      <header className="mb-2 flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center">
          <Skeleton className="h-8 w-8 rounded-md" />
          <div className="ml-4 flex flex-col gap-1">
            <Skeleton className="h-6 w-56" />
            <Skeleton className="h-4 w-40" />
          </div>
        </div>
        <Skeleton className="ml-3 h-8 w-40 rounded-md" />
      </header>

      {/* Question statistics cards */}
      <div className="flex flex-col gap-4">
        {Array.from({ length: QUESTION_SKELETON_COUNT }).map((_, i) => (
          <StatisticsQuestionSkeleton key={`stats-skeleton-${i}`} />
        ))}
      </div>
    </section>
  );
};
