"use client";

import { Button } from "@/core/components/ui/button";
import { Spinner } from "@/core/components/ui/spinner";
import { ICONS } from "@/core/config/icons";
import { FormBaseFormSubmitComponentProps } from "@/features/form/components/form-base-form";
import { useTranslations } from "next-intl";

export const FormCreateFormSubmitButton = ({
  isPending,
  uploadProgressPercent,
}: FormBaseFormSubmitComponentProps) => {
  const t = useTranslations("formCreatePage");

  return (
    <Button type="submit" className="ml-auto min-w-40" disabled={isPending}>
      {isPending ? <Spinner /> : <ICONS.save />}
      {isPending ? t("submitting") : t("submit")}
      {uploadProgressPercent ? ` (${uploadProgressPercent}%)` : ""}
    </Button>
  );
};
