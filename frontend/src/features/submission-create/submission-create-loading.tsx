import { Card } from "@/core/components/ui/card";
import { Skeleton } from "@/core/components/ui/skeleton";

const QUESTION_SKELETON_COUNT = 3;

const QuestionCardSkeleton = () => (
  <Card className="gap-2 p-4">
    {/* Question header */}
    <div className="flex flex-col flex-wrap gap-1 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex gap-1">
        <Skeleton className="h-5 w-6" />
        <Skeleton className="h-5 w-48" />
      </div>
      <Skeleton className="ml-3 h-4 w-24" />
    </div>

    {/* Answer area */}
    <Skeleton className="h-24 w-full rounded-md" />
  </Card>
);

export const SubmissionCreateLoading = () => {
  return (
    <section className="flex w-full flex-col gap-4 px-5 py-10 lg:px-30">
      <header className="flex flex-col">
        {/* Banner image - mobile */}
        <Skeleton className="h-64 w-full rounded-t-md rounded-b-none md:hidden" />
        {/* Banner image - desktop */}
        <Skeleton className="hidden h-110 w-full rounded-t-md rounded-b-none md:block" />

        {/* Form info card */}
        <Card className="flex flex-col gap-4 rounded-t-none p-4">
          <Skeleton className="h-7 w-64" />
          <div className="flex flex-col gap-1.5">
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-4 w-3/5" />
          </div>
        </Card>
      </header>

      {/* Question cards */}
      {Array.from({ length: QUESTION_SKELETON_COUNT }).map((_, i) => (
        <QuestionCardSkeleton key={`question-skeleton-${i}`} />
      ))}

      {/* Submit button */}
      <footer className="flex justify-end gap-4">
        <Skeleton className="h-10 w-40 rounded-md" />
      </footer>
    </section>
  );
};
