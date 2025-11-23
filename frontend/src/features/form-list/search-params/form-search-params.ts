import { Language } from "@/core/types/form";
import { parseAsDuration } from "@/features/form-list/search-params/parseAsDuration";
import {
  createLoader,
  parseAsBoolean,
  parseAsString,
  parseAsStringEnum,
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
  sort: parseAsString,
} as const;

export const loadFormSortSearchParams = createLoader(formSortSearchParams);
