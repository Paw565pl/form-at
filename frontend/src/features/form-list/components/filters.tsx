import { Input } from "@/core/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/core/components/ui/select";
import { useTranslations } from "next-intl";
import { parseAsStringLiteral, useQueryState } from "nuqs";
import { useState } from "react";

export const Filters = () => {
  const [query, setQuery] = useQueryState("query", { defaultValue: "" });
  const [queryValue, setQueryValue] = useState(query);
  const t = useTranslations("formListPage");

  const sortOptions = [
    { key: "createdAt", label: t("options.sortOptions.newest") },
    { key: "submissionsCount", label: t("options.sortOptions.submissions") },
    { key: "estimatedDuration", label: t("options.sortOptions.duration") },
  ];

  const [sortBy, setSortBy] = useQueryState(
    "sortBy",
    parseAsStringLiteral(sortOptions.map((opt) => opt.key)).withDefault(
      "createdAt",
    ),
  );

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

      <Select value={sortBy} onValueChange={setSortBy}>
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
