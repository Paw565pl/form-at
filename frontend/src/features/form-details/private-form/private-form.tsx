"use client";

import { Button } from "@/core/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/core/components/ui/dialog";
import { Field, FieldError, FieldLabel } from "@/core/components/ui/field";
import { Input } from "@/core/components/ui/input";
import { Comments } from "@/features/form-details/comments/comments";
import { Banner } from "@/features/form-details/components/banner";
import { Details } from "@/features/form-details/components/details";
import { QuestionList } from "@/features/form-details/components/question-list/question-list";
import { useFetchPrivateFormDetails } from "@/features/form-details/private-form/hooks/use-fetch-private-form-details";
import { getPrivateFormSchema } from "@/features/form-details/private-form/schemas/private-form-schema";
import { zodResolver } from "@hookform/resolvers/zod";
import { HttpStatusCode } from "axios";
import { useTranslations } from "next-intl";
import { useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import z from "zod";

type PrivateFormData = z.infer<ReturnType<typeof getPrivateFormSchema>>;

interface PrivateFormProps {
  readonly formIdOrSlug: string;
}

export const PrivateForm = ({ formIdOrSlug }: PrivateFormProps) => {
  const t = useTranslations("formDetailsPage.privateForm");

  const privateFormSchema = getPrivateFormSchema(t);
  const form = useForm<PrivateFormData>({
    resolver: zodResolver(privateFormSchema),
    defaultValues: {
      code: "",
    },
  });

  // manually trigger validation if locale has changed
  useEffect(() => {
    if (Object.keys(form.formState.errors).length > 0) form.trigger();
  }, [form, t]);

  // eslint-disable-next-line react-hooks/incompatible-library
  const code = form.watch("code");

  const {
    data: privateForm,
    refetch,
    isFetching,
  } = useFetchPrivateFormDetails(formIdOrSlug, code, {
    enabled: false,
    retry: false,
  });

  const onSubmit = async () => {
    form.clearErrors("code");

    const result = await refetch();

    if (result.error) {
      const message =
        result.error.response?.data?.message ?? result.error.message;
      if (result.error.response?.status === HttpStatusCode.Forbidden) {
        form.setError("code", { message: t("errors.invalidCode") });
        return;
      } else {
        form.setError("code", { message });
      }
      return;
    }
  };

  return (
    <>
      {privateForm ? (
        <section id="private-form-details" className="px-5 py-10 lg:px-30">
          <Banner form={privateForm} />
          <Details form={privateForm} />
          <QuestionList form={privateForm} />
          <Comments formIdOrSlug={formIdOrSlug} />
        </section>
      ) : null}

      <Dialog open={!privateForm}>
        <DialogContent className="[&>button]:hidden">
          <form
            className="flex flex-col gap-2"
            onSubmit={form.handleSubmit(onSubmit)}
          >
            <DialogHeader>
              <DialogTitle>{t("enterTheCode")}</DialogTitle>
              <DialogDescription>{t("codeDescription")}</DialogDescription>
            </DialogHeader>

            <Controller
              name="code"
              control={form.control}
              rules={{
                required: t("enterTheCode"),
              }}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>{t("code")}</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="password"
                    placeholder={t("code")}
                    aria-invalid={fieldState.invalid}
                    disabled={isFetching}
                    onChange={(e) => {
                      field.onChange(e);
                      if (form.formState.errors.code) form.clearErrors("code");
                    }}
                  />
                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </Field>
              )}
            />

            <DialogFooter>
              <Button type="submit" disabled={isFetching}>
                {t("confirm")}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </>
  );
};
