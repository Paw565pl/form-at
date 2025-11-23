import { Input } from "@/core/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/core/components/ui/select";
import { FormSortOptions } from "@/core/types/form";
import {
  formFilterSearchParams,
  formSortSearchParams,
} from "@/features/form-list/search-params/form-search-params";
import { useTranslations } from "next-intl";
import { useQueryStates } from "nuqs";
import { useRef } from "react";

export const Filters = () => {
  const t = useTranslations("formListPage");
  const [, setFilters] = useQueryStates(formFilterSearchParams);
  const [{ sort }, setSort] = useQueryStates(formSortSearchParams);
  const searchInputRef = useRef<HTMLInputElement>(null);

  const sortOptions: {
    key: FormSortOptions;
    label: string;
  }[] = [
    { key: "updatedAt,desc", label: t("options.sortOptions.newest") },
    { key: "updatedAt,asc", label: t("options.sortOptions.oldest") },
    {
      key: "submissionsCount,desc",
      label: t("options.sortOptions.submissions"),
    },
    { key: "estimatedDuration,desc", label: t("options.sortOptions.duration") },
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
          placeholder={t("options.searchPlaceholder")}
          type="search"
          className="md:min-w-60"
        />
      </form>

      <Select
        value={sort ?? undefined}
        onValueChange={(newValue) =>
          setSort({ sort: newValue as FormSortOptions })
        }
      >
        <SelectTrigger className="w-full max-w-70">
          {t("options.sortBy")}
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
