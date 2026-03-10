"use client";

import { FormListLayout } from "@/features/form-list/components/forms";
import { FORM_LIST_LAYOUT_COOKIE_KEY } from "@/features/form-list/constants/form-list-layout-cookie-key";
import { useState } from "react";

export const useFormListLayout = (initialValue?: string) => {
  const [formListLayout, _setFormListLayout] = useState<FormListLayout>(() => {
    if (initialValue === "grid" || initialValue === "list") return initialValue;
    else return "grid";
  });

  const setFormListLayout = (newValue: FormListLayout) => {
    window.cookieStore.set(FORM_LIST_LAYOUT_COOKIE_KEY, newValue);
    _setFormListLayout(newValue);
  };

  return [formListLayout, setFormListLayout] as const;
};
