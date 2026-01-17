"use client";

import { FormRequest } from "@/core/types/form";
import { useCreateForm } from "@/features/form-create/hooks/use-create-form";
import { FormBaseForm } from "@/features/form/components/form-base-form";
import { AxiosError } from "axios";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

export const FormCreateForm = () => {
  const t = useTranslations("formCreatePage");

  const router = useRouter();
  const {
    mutate: createForm,
    isPending,
    uploadProgressPercent,
  } = useCreateForm();

  const onSubmit = (request: FormRequest) => {
    createForm(request, {
      onSuccess: (data) => {
        toast.success(t("successMessage"));
        router.replace(`/forms/${data.slug}`);
      },
      onError: (error) => {
        if (error instanceof AxiosError) {
          toast.error(error.response?.data.message);
        } else {
          toast.error(t("errorMessage"));
        }
      },
    });
  };

  return (
    <FormBaseForm
      pageTitle={t("pageTitle")}
      isPending={isPending}
      uploadProgressPercent={uploadProgressPercent}
      onSubmit={onSubmit}
    />
  );
};
