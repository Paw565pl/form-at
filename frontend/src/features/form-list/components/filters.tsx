import { Input } from "@/core/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/core/components/ui/select";
import { FormSortOption } from "@/core/types/form";
import {
  formFilterSearchParams,
  formSortSearchParams,
} from "@/features/form-list/search-params/form-search-params";
import { useTranslations } from "next-intl";
import { useQueryStates } from "nuqs";
import { useRef } from "react";

export const Filters = () => {
  const t = useTranslations("formListPage.options");
  const [{ searchQuery }, setFilters] = useQueryStates(formFilterSearchParams);
  const [{ sort }, setSort] = useQueryStates(formSortSearchParams);
  const searchInputRef = useRef<HTMLInputElement>(null);

  const sortOptions: {
    key: FormSortOption;
    label: string;
  }[] = [
    { key: "createdAt,desc", label: t("sortOptions.createdAt,desc") },
    { key: "createdAt,asc", label: t("sortOptions.createdAt,asc") },
    { key: "questionsCount,asc", label: t("sortOptions.questionsCount,asc") },
    { key: "questionsCount,desc", label: t("sortOptions.questionsCount,desc") },
    {
      key: "submissionsCount,asc",
      label: t("sortOptions.submissionsCount,asc"),
    },
    {
      key: "submissionsCount,desc",
      label: t("sortOptions.submissionsCount,desc"),
    },
    {
      key: "estimatedDuration,asc",
      label: t("sortOptions.estimatedDuration,asc"),
    },
    {
      key: "estimatedDuration,desc",
      label: t("sortOptions.estimatedDuration,desc"),
    },
  ] as const;

  return (
    <div className="flex w-full flex-wrap gap-2 md:flex-nowrap">
      <form
        className="w-full max-w-70"
        onSubmit={(e) => {
          e.preventDefault();
          const searchQuery = searchInputRef.current?.value.trim() || null;

          setFilters({ searchQuery });
        }}
      >
        <Input
          ref={searchInputRef}
          defaultValue={searchQuery ?? undefined}
          placeholder={t("searchPlaceholder")}
          type="search"
          className="md:min-w-60"
        />
      </form>

      <Select
        value={sort ?? undefined}
        onValueChange={(newValue) =>
          setSort({ sort: newValue as FormSortOption })
        }
      >
        <SelectTrigger
          aria-label={t("sortBy")}
          className="w-full max-w-70 md:max-w-54"
        >
          {t("sortBy")}
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          {sortOptions.map((option) => (
            <SelectItem key={option.key} value={option.key}>
              {option.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
};
