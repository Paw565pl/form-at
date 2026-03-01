import { Card } from "@/core/components/ui/card";
import { Skeleton } from "@/core/components/ui/skeleton";

const QUESTION_SKELETON_COUNT = 4;

const QuestionCardSkeleton = () => (
  <Card className="gap-2 p-4">
    {/* Question header */}
    <div className="flex flex-col flex-wrap gap-1 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex gap-1">
        <Skeleton className="h-5 w-6" />
        <Skeleton className="h-5 w-48" />
      </div>
      <Skeleton className="h-4 w-24 pl-3" />
    </div>

    {/* Answer content */}
    <div className="flex flex-col gap-2 px-3">
      <Skeleton className="h-4 w-3/4" />
      <Skeleton className="h-4 w-1/2" />
    </div>
  </Card>
);

export const SubmissionDetailsLoading = () => {
  return (
    <section className="flex w-full flex-col gap-4 px-5 py-10 lg:px-30">
      {/* Header: back button + author name + delete button */}
      <header className="flex gap-2">
        <Skeleton className="h-8 w-8" />
        <Skeleton className="h-7 w-52" />
        <Skeleton className="ml-auto h-8 w-8" />
      </header>

      {/* Question cards */}
      <div className="flex flex-col gap-4">
        {Array.from({ length: QUESTION_SKELETON_COUNT }).map((_, i) => (
          <QuestionCardSkeleton key={i} />
        ))}
      </div>
    </section>
  );
};
