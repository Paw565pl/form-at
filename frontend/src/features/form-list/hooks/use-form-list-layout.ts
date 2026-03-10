"use client";

import { FormListLayout } from "@/features/form-list/components/forms";
import { FORM_LIST_LAYOUT_COOKIE_KEY } from "@/features/form-list/utils/parse-form-list-layout";
import { useState } from "react";

export const useFormListLayout = (initialValue: FormListLayout) => {
  const [formListLayout, _setFormListLayout] =
    useState<FormListLayout>(initialValue);

  const setFormListLayout = (newValue: FormListLayout) => {
    window.cookieStore.set(FORM_LIST_LAYOUT_COOKIE_KEY, newValue);
    _setFormListLayout(newValue);
  };

  return [formListLayout, setFormListLayout] as const;
};
