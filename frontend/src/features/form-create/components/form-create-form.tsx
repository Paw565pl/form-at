"use client";

import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormRequest } from "@/core/types/form";
import { FormCreateFormSubmitButton } from "@/features/form-create/components/form-create-form-submit-button";
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
          const errorResponse = error.response?.data as ErrorResponseDto;
          if (errorResponse.code === "FORM_ALREADY_EXISTS") {
            toast.error(t("errors.formAlreadyExists"));
          } else if (errorResponse.code === "USER_UPLOAD_RATE_LIMIT_EXCEEDED") {
            toast.error(t("errors.uploadQuotaExceeded"));
          } else {
            toast.error(t("errors.unexpected"));
          }
        } else {
          toast.error(t("errors.unexpected"));
        }
      },
    });
  };

  return (
    <FormBaseForm
      pageTitle={t("pageTitle")}
      isPending={isPending}
      uploadProgressPercent={uploadProgressPercent}
      SubmitComponent={FormCreateFormSubmitButton}
      onSubmit={onSubmit}
    />
  );
};
