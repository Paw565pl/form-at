import { FormListLayout } from "@/features/form-list/components/forms";

export const FORM_LIST_LAYOUT_COOKIE_KEY = "form-list-layout";

export const parseFormListLayout = (value?: string): FormListLayout => {
  if (value === "grid" || value === "list") return value;
  else return "grid";
};
