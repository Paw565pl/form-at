import { Card } from "@/core/components/ui/card";
import { Skeleton } from "@/core/components/ui/skeleton";

const QuestionCardSkeleton = () => (
  <Card className="gap-4 p-4">
    {/* Question header */}
    <div className="flex items-center justify-between gap-8">
      <Skeleton className="ml-3 h-5 w-28" />
      <Skeleton className="h-8 w-8" />
    </div>

    {/* Question content input */}
    <div className="flex flex-col gap-2">
      <Skeleton className="mx-3 h-4 w-28" />
      <Skeleton className="h-9 w-full" />
    </div>

    {/* Image upload */}
    <div className="flex flex-col gap-2">
      <Skeleton className="mx-3 h-4 w-24" />
      <Skeleton className="h-9 w-full" />
    </div>

    {/* Type selector + required checkbox */}
    <div className="flex flex-col items-center justify-between gap-4 sm:flex-row">
      <Skeleton className="h-9 w-full sm:w-40" />
      <Skeleton className="h-5 w-24" />
    </div>
  </Card>
);

const QUESTION_SKELETON_COUNT = 2;

export const FormCreateLoading = () => {
  return (
    <section className="flex w-full flex-col gap-4 px-5 py-10 lg:px-30">
      {/* Page title */}
      <Skeleton className="ml-4 h-7 w-48" />

      <div className="flex flex-col gap-4 md:grid md:grid-cols-3">
        {/* Main form card */}
        <Card className="col-span-2 gap-4 p-4">
          {/* Name field */}
          <div className="flex flex-col gap-2">
            <div className="flex items-end justify-between px-3">
              <Skeleton className="h-4 w-16" />
              <Skeleton className="h-3 w-12" />
            </div>
            <Skeleton className="h-9 w-full" />
          </div>

          {/* Description field */}
          <div className="flex flex-col gap-2">
            <div className="flex items-end justify-between px-3">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-3 w-12" />
            </div>
            <Skeleton className="h-24 w-full" />
          </div>

          {/* Status selector */}
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
            <Skeleton className="h-9 w-full sm:w-48" />
            <Skeleton className="ml-3 h-4 w-40 sm:ml-0" />
          </div>

          {/* Thanks message field */}
          <div className="flex flex-col gap-2">
            <div className="flex items-end justify-between px-3">
              <Skeleton className="h-4 w-28" />
              <Skeleton className="h-3 w-12" />
            </div>
            <Skeleton className="h-24 w-full" />
          </div>
        </Card>

        {/* Settings card */}
        <Card className="gap-4 p-4">
          {/* Thumbnail upload */}
          <div className="flex flex-col gap-2">
            <div className="flex items-end justify-between px-3">
              <Skeleton className="h-4 w-28" />
              <Skeleton className="h-3 w-16" />
            </div>
            <Skeleton className="h-9 w-full" />
          </div>

          {/* Language select */}
          <Skeleton className="h-9 w-full" />

          {/* Shuffle select */}
          <Skeleton className="h-9 w-full" />

          {/* Duration select */}
          <Skeleton className="h-9 w-full" />

          {/* Checkbox options */}
          <div className="flex items-center justify-between px-3">
            <Skeleton className="h-4 w-40" />
            <Skeleton className="h-5 w-5 rounded" />
          </div>
          <div className="flex items-center justify-between px-3">
            <Skeleton className="h-4 w-44" />
            <Skeleton className="h-5 w-5 rounded" />
          </div>
          <div className="flex items-center justify-between px-3">
            <Skeleton className="h-4 w-36" />
            <Skeleton className="h-5 w-5 rounded" />
          </div>
        </Card>
      </div>

      {/* Questions header */}
      <div className="flex items-center justify-between">
        <Skeleton className="ml-4 h-6 w-32" />
        <div className="flex gap-2">
          <Skeleton className="h-8 w-28" />
          <Skeleton className="h-8 w-8" />
        </div>
      </div>

      {/* Question cards */}
      {Array.from({ length: QUESTION_SKELETON_COUNT }).map((_, i) => (
        <QuestionCardSkeleton key={i} />
      ))}

      {/* Submit button */}
      <div className="flex justify-end">
        <Skeleton className="h-10 w-40" />
      </div>
    </section>
  );
};
