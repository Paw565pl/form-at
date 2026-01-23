"use client";

import { Button } from "@/core/components/ui/button";
import { Spinner } from "@/core/components/ui/spinner";
import { ICONS } from "@/core/config/icons";
import { useTranslations } from "next-intl";

interface FormCreateFormSubmitButtonProps {
  readonly isFormPending: boolean;
  readonly uploadProgressPercent: number | null;
}

export const FormCreateFormSubmitButton = ({
  isFormPending,
  uploadProgressPercent,
}: FormCreateFormSubmitButtonProps) => {
  const t = useTranslations("formCreatePage");

  return (
    <Button type="submit" className="ml-auto min-w-40" disabled={isFormPending}>
      {isFormPending ? <Spinner /> : <ICONS.save />}
      {isFormPending ? t("submitting") : t("submit")}
      {uploadProgressPercent ? ` (${uploadProgressPercent}%)` : ""}
    </Button>
  );
};
