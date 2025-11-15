import { Language } from "@/core/types/form";

export interface FormFilterOptionsDto {
  searchQuery?: string;
  language?: Language;
  minEstimatedDuration?: string;
  maxEstimatedDuration?: string;
  allowsGuestSubmissions?: boolean;
}
