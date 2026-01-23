"use client";

import { FormRequest } from "@/core/types/form";
import { useFetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { FormEditFormSubmitAlertDialog } from "@/features/form-edit/components/form-edit-form-submit-alert-dialog";
import { useUpdateForm } from "@/features/form-edit/hooks/use-update-form";
import { FormBaseForm } from "@/features/form/components/form-base-form";
import { AxiosError, HttpStatusCode } from "axios";
import { useTranslations } from "next-intl";
import { notFound, useRouter } from "next/navigation";
import { toast } from "sonner";

interface FormEditFormProps {
  readonly slug: string;
}

export const FormEditForm = ({ slug }: FormEditFormProps) => {
  const t = useTranslations("formEditPage");

  const router = useRouter();
  const {
    data: formDetails,
    isLoading,
    error,
  } = useFetchFormDetails(slug, { subscribed: false });
  const {
    mutate: updateForm,
    isPending,
    uploadProgressPercent,
  } = useUpdateForm(slug);

  if (error) {
    if (error.status === HttpStatusCode.NotFound) return notFound();
    else throw error;
  }

  if (isLoading || !formDetails) return <p>{t("loading")}</p>;

  const onSubmit = (request: FormRequest) => {
    updateForm(request, {
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
      submitButton={
        <FormEditFormSubmitAlertDialog
          isFormPending={isPending}
          uploadProgressPercent={uploadProgressPercent}
        />
      }
      onSubmit={onSubmit}
      defaultValues={formDetails}
    />
  );
};
