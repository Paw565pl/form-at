import { Input } from "@/core/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/core/components/ui/select";
import { sortByParser } from "@/features/form-list/search-params/sort-by-parser";
import { useTranslations } from "next-intl";
import { useQueryState } from "nuqs";
import { useState } from "react";

export const Filters = () => {
  const [sortBy, setSortBy] = useQueryState(
    "sortBy",
    sortByParser.withDefault("newest"),
  );
  const [query, setQuery] = useQueryState("query", { defaultValue: "" });
  const [queryValue, setQueryValue] = useState(query);
  const t = useTranslations("formListPage");

  const sortOptions = [
    { key: "newest", label: t("options.sortOptions.newest") },
    { key: "oldest", label: t("options.sortOptions.oldest") },
    { key: "name", label: t("options.sortOptions.name") },
    { key: "questions", label: t("options.sortOptions.questions") },
    { key: "submissions", label: t("options.sortOptions.submissions") },
    { key: "duration", label: t("options.sortOptions.duration") },
  ];

  return (
    <div className="flex w-full flex-wrap gap-2 md:flex-nowrap">
      <form
        className="w-full max-w-70"
        onSubmit={(e) => {
          e.preventDefault();
          setQuery(queryValue);
        }}
      >
        <Input
          placeholder={t("options.searchPlaceholder")}
          type="search"
          className="md:min-w-60"
          value={queryValue}
          onChange={(e) => setQueryValue(e.target.value)}
        />
      </form>

      <Select value={sortBy} onValueChange={(value) => setSortBy(value)}>
        <SelectTrigger className="w-full max-w-70">
          {t("options.sortBy")}
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          {sortOptions.map((opt) => (
            <SelectItem key={opt.key} value={opt.key}>
              {opt.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
};
