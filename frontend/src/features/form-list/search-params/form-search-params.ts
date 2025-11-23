import { formSortOptions, Language } from "@/core/types/form";
import { parseAsDuration } from "@/features/form-list/search-params/parseAsDuration";
import {
  createLoader,
  parseAsBoolean,
  parseAsString,
  parseAsStringEnum,
  parseAsStringLiteral,
} from "nuqs/server";

export const formFilterSearchParams = {
  searchQuery: parseAsString,
  language: parseAsStringEnum<Language>(Object.values(Language)),
  minEstimatedDuration: parseAsDuration,
  maxEstimatedDuration: parseAsDuration,
  allowsGuestSubmissions: parseAsBoolean,
} as const;

export const loadFormFilterSearchParams = createLoader(formFilterSearchParams);

export const formSortSearchParams = {
  sort: parseAsStringLiteral(formSortOptions).withDefault("updatedAt,desc"),
} as const;

export const loadFormSortSearchParams = createLoader(formSortSearchParams);
