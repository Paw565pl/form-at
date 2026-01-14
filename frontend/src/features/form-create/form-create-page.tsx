"use client";

import { FormRequest } from "@/core/types/form";
import { useCreateForm } from "@/features/form-create/hooks/use-create-form";
import { FormBaseForm } from "@/features/form/components/form-base-form";
import { AxiosError } from "axios";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

export const FormCreatePage = () => {
  const t = useTranslations("formCreatePage");
  const router = useRouter();
  const createForm = useCreateForm();

  const onSubmit = (request: FormRequest) => {
    createForm.mutate(request, {
      onSuccess: (data) => {
        toast.success(t("formCreated"));
        router.push(`/forms/${data.slug}`);
      },
      onError: (error) => {
        toast.error(
          (error instanceof AxiosError && error.response?.data?.message) ||
            t("errors.unexpected"),
        );
      },
    });
  };

  return <FormBaseForm onSubmit={onSubmit} />;
};
