import { Card } from "@/core/components/ui/card";
import { Skeleton } from "@/core/components/ui/skeleton";
import { FormListItemSkeleton } from "@/features/form-list/form-list-loading";

const FORM_LIST_ITEM_COUNT = 3;

export const UserProfileLoading = () => {
  return (
    <section className="flex w-full flex-col gap-4 px-5 py-10 lg:px-30">
      <div className="flex flex-col gap-4 md:flex-row">
        {/* Profile Header Card */}
        <Card className="flex flex-1 flex-col items-center justify-center gap-2 p-3">
          <Skeleton className="h-48 w-48 rounded-full" />
          <Skeleton className="h-8 w-40" />
        </Card>

        {/* User Forms */}
        <div className="flex flex-2 flex-col gap-2">
          <Skeleton className="h-6 w-32" />
          {Array.from({ length: FORM_LIST_ITEM_COUNT }).map((_, i) => (
            <FormListItemSkeleton key={i} />
          ))}
        </div>
      </div>

      {/* Statistics header */}
      <Skeleton className="h-6 w-36" />

      {/* Statistics cards */}
      <div className="flex flex-col gap-4 sm:flex-row">
        <Card className="flex flex-1 flex-col items-center rounded-md border p-4">
          <Skeleton className="h-7 w-12" />
          <Skeleton className="mt-1 h-4 w-24" />
        </Card>
        <Card className="flex flex-1 flex-col items-center rounded-md border p-4">
          <Skeleton className="h-7 w-12" />
          <Skeleton className="mt-1 h-4 w-24" />
        </Card>
        <Card className="flex flex-1 flex-col items-center rounded-md border p-4">
          <Skeleton className="h-7 w-12" />
          <Skeleton className="mt-1 h-4 w-24" />
        </Card>
      </div>
    </section>
  );
};
