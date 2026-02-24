import { Card } from "@/core/components/ui/card";
import { Skeleton } from "@/core/components/ui/skeleton";

export const GridCardSkeleton = () => (
  <Card className="h-full overflow-hidden">
    {/* Image area */}
    <Skeleton className="min-h-40 rounded-none" />

    {/* Content area */}
    <div className="flex flex-col gap-2 p-3">
      {/* Header: name + author */}
      <div className="flex flex-wrap items-center justify-between gap-2">
        <Skeleton className="h-5 w-2/5" />
        <Skeleton className="h-4 w-1/4" />
      </div>

      {/* Description */}
      <div className="flex flex-col gap-1.5">
        <Skeleton className="h-3.5 w-full" />
        <Skeleton className="h-3.5 w-full" />
        <Skeleton className="h-3.5 w-4/5" />
      </div>

      {/* Footer: stats + date */}
      <div className="mt-auto flex flex-wrap justify-between gap-1">
        <Skeleton className="h-3.5 w-2/5" />
        <Skeleton className="h-3.5 w-1/4" />
      </div>
    </div>
  </Card>
);

const GRID_SKELETON_COUNT = 6;

export const FormListLoading = () => {
  return (
    <section className="flex w-full flex-col gap-2 px-5 py-10 lg:px-30">
      {/* Header */}
      <header className="flex flex-col flex-wrap justify-between gap-4 md:flex-row">
        {/* Title */}
        <Skeleton className="h-8 w-40" />

        <div className="flex flex-col justify-between gap-2 md:flex-row">
          {/* Filters */}
          <div className="flex w-full flex-col gap-2 md:flex-row">
            {/* Search input */}
            <Skeleton className="h-9 w-full max-w-70 min-w-60" />
            {/* Sort select */}
            <Skeleton className="h-9 w-full max-w-70 min-w-36 md:max-w-26" />
          </div>

          {/* View toggle buttons */}
          <div className="flex gap-2">
            <Skeleton className="h-9 w-9" />
            <Skeleton className="h-9 w-9" />
          </div>
        </div>
      </header>

      {/* Grid view skeleton */}
      <div className="grid grid-cols-1 gap-4 pt-2 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: GRID_SKELETON_COUNT }).map((_, i) => (
          <GridCardSkeleton key={`grid-skeleton-${i}`} />
        ))}
      </div>
    </section>
  );
};
