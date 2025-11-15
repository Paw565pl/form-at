import { SortOptionsDto } from "@/core/types/sort-options-dto";

export const formSortOptions = {
  "estimatedDuration:asc": new SortOptionsDto("estimatedDuration", "asc"),
  "estimatedDuration:desc": new SortOptionsDto("estimatedDuration", "desc"),

  "submissionsCount:asc": new SortOptionsDto("submissionsCount", "asc"),
  "submissionsCount:desc": new SortOptionsDto("submissionsCount", "desc"),

  "createdAt:asc": new SortOptionsDto("createdAt", "asc"),
  "createdAt:desc": new SortOptionsDto("createdAt", "desc"),

  "updatedAt:asc": new SortOptionsDto("updatedAt", "asc"),
  "updatedAt:desc": new SortOptionsDto("updatedAt", "desc"),
} as const;
