"use client";

import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { Checkbox } from "@/core/components/ui/checkbox";
import { Field, FieldError } from "@/core/components/ui/field";
import { Label } from "@/core/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/core/components/ui/radio-group";
import { Spinner } from "@/core/components/ui/spinner";
import { Textarea } from "@/core/components/ui/textarea";
import { ICONS } from "@/core/config/icons";
import { FormImageWithFallback } from "@/core/form-image/form-image-with-fallback";
import { FormDetailResponseDto } from "@/core/types/form";
import {
  SubmissionAnswerDto,
  SubmissionRequestDto,
} from "@/core/types/submission";
import { useCreateSubmission } from "@/features/submission-create/hooks/use-create-submission";
import { submissionSchema } from "@/features/submission-create/schemas/submission-schemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { AxiosError } from "axios";
import { useTranslations } from "next-intl";
import { useState } from "react";
import type { FieldError as FieldErrorType } from "react-hook-form";
import { Controller, useForm } from "react-hook-form";
import { toast } from "sonner";
import z from "zod";

// TODO
// translating errors
// fill anonymously functionality (?)
// cleanup

interface SubmissionProps {
  readonly formData: FormDetailResponseDto;
}

// temp
import en from "@/../messages/en.json";

function getTranslatedErrors(
  translator: ReturnType<typeof useTranslations>,
  pageName: keyof typeof en,
  error?: FieldErrorType,
): { message: string }[] {
  const pageMessages = en[pageName];
  if (!("errors" in pageMessages)) {
    throw new Error(`No errors found for page: ${pageName}`);
  }
  type ErrorKey = keyof (typeof pageMessages)["errors"];

  return error
    ? [
        {
          message: translator(`errors.${error.message as ErrorKey}`),
        },
      ]
    : [];
}

export const Submission = ({ formData }: SubmissionProps) => {
  const t = useTranslations("submissionCreatePage");
  const createSubmission = useCreateSubmission(formData.id);
  const [fillAnonymously, setFillAnonymously] = useState(false);

  const form = useForm<z.infer<typeof submissionSchema>>({
    resolver: zodResolver(submissionSchema),
    defaultValues: {
      answers: formData.questions.map((q) => ({
        questionId: q.id,
        chosenAnswerIds: [],
        openAnswer: "",
        type: q.type,
        required: q.isRequired,
      })),
    },
  });

  const onSubmit = (data: z.infer<typeof submissionSchema>) => {
    const request: SubmissionRequestDto = {
      ...data,
      answers: data.answers
        .filter(
          // filter out empty answers for non-required questions
          (answer: SubmissionAnswerDto) =>
            answer.chosenAnswerIds.length > 0 || answer.openAnswer !== "",
        )
        .map((answer: SubmissionAnswerDto) => ({
          questionId: answer.questionId,
          chosenAnswerIds: answer.chosenAnswerIds,
          openAnswer: answer.openAnswer || null,
        })),
    };

    createSubmission.mutate(request, {
      onError: (error) => {
        toast.error(
          (error instanceof AxiosError && error.response?.data?.message) ||
            t("errors.unexpected"),
        );
      },
    });
  };

  return (
    <section
      id="form-create"
      className="flex w-full flex-col gap-4 px-5 py-10 lg:px-30"
    >
      <header className="flex flex-col">
        <div className="relative h-48">
          <FormImageWithFallback
            src={formData.thumbnail}
            alt="Background"
            fill
            style={{ objectFit: "cover" }}
            preload
            className="rounded-t-md"
          />
        </div>

        <Card className="flex flex-col gap-4 rounded-t-none p-4">
          <header className="flex flex-wrap items-center gap-2 md:flex-row">
            <h1 className="line-clamp-2 max-w-2xl text-2xl">{formData.name}</h1>
          </header>
          {formData.description && <p>{formData.description}</p>}
        </Card>
      </header>

      {!createSubmission.isSuccess && (
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="flex flex-col gap-4"
        >
          {formData.questions.map((question, index) => {
            return (
              <Card key={question.id} className="gap-2 p-4">
                <header className="flex flex-col gap-4">
                  {question.image && (
                    <div className="relative h-60">
                      <FormImageWithFallback
                        src={question.image}
                        alt="Background"
                        fill
                        style={{ objectFit: "contain" }}
                        preload
                        className="rounded-md"
                      />
                    </div>
                  )}

                  <div className="flex gap-1">
                    <span className="text-muted-foreground">{index + 1}.</span>
                    <h2 className="font-medium">{question.content}</h2>

                    {question.isRequired && (
                      <span className="text-muted-foreground">*</span>
                    )}
                    <span className="text-muted-foreground ml-auto text-sm">
                      {t(`questionTypes.${question.type}`)}
                    </span>
                  </div>
                </header>

                {question.type === "OPEN" && (
                  <Controller
                    name={`answers.${index}.openAnswer`}
                    control={form.control}
                    render={({ field, fieldState }) => (
                      <Field data-invalid={fieldState.invalid}>
                        <Textarea
                          {...field}
                          id="open-answer"
                          aria-invalid={fieldState.invalid}
                          autoComplete="off"
                          className="min-h-24"
                          disabled={createSubmission.isPending}
                          maxLength={1000}
                        />
                        {fieldState.invalid && (
                          <FieldError
                            className="mx-3 max-w-fit"
                            errors={getTranslatedErrors(
                              t,
                              "submissionCreatePage",
                              fieldState.error,
                            )}
                            // errors={[fieldState.error]} // translate this
                          />
                        )}
                      </Field>
                    )}
                  />
                )}

                {question.type === "SINGLE_CHOICE" && (
                  <Controller
                    name={`answers.${index}.chosenAnswerIds`}
                    control={form.control}
                    render={({ field, fieldState }) => (
                      <Field data-invalid={fieldState.invalid}>
                        <RadioGroup
                          disabled={createSubmission.isPending}
                          onValueChange={(value) => {
                            field.onChange([value]);
                          }}
                        >
                          {question.answers.map((answer) => (
                            <div
                              key={answer.id}
                              className="flex items-center gap-2"
                            >
                              <RadioGroupItem
                                value={answer.id}
                                id={answer.id}
                              />
                              <Label
                                htmlFor={answer.id}
                                className={
                                  createSubmission.isPending
                                    ? "text-muted-foreground cursor-not-allowed"
                                    : ""
                                }
                              >
                                {answer.content}
                              </Label>
                            </div>
                          ))}
                        </RadioGroup>
                        {fieldState.invalid && (
                          <FieldError
                            className="mx-3 max-w-fit"
                            // errors={getTranslatedErrors(fieldState.error)}
                            errors={[fieldState.error]} // translate this
                          />
                        )}
                      </Field>
                    )}
                  />
                )}

                {question.type === "MULTIPLE_CHOICE" && (
                  <Controller
                    name={`answers.${index}.chosenAnswerIds`}
                    control={form.control}
                    render={({ field, fieldState }) => (
                      <Field data-invalid={fieldState.invalid}>
                        <div className="flex flex-col gap-2">
                          {question.answers.map((answer) => (
                            <div
                              key={answer.id}
                              className="flex items-center gap-2"
                            >
                              <Checkbox
                                checked={field.value.includes(answer.id)}
                                id={answer.id}
                                disabled={createSubmission.isPending}
                                onCheckedChange={(checked) => {
                                  if (checked) {
                                    field.onChange([...field.value, answer.id]);
                                  } else {
                                    field.onChange(
                                      field.value.filter(
                                        (id) => id !== answer.id,
                                      ),
                                    );
                                  }
                                }}
                              />
                              <Label htmlFor={answer.id}>
                                {answer.content}
                              </Label>
                            </div>
                          ))}
                        </div>
                        {fieldState.invalid && (
                          <FieldError
                            className="mx-3 max-w-fit"
                            // errors={getTranslatedErrors(fieldState.error)}
                            errors={[fieldState.error]} // translate this
                          />
                        )}
                      </Field>
                    )}
                  />
                )}
              </Card>
            );
          })}

          <footer className="flex justify-end gap-4">
            <div className="flex items-center gap-2">
              <Checkbox
                id="fillAnonymously"
                className="max-w-5"
                checked={fillAnonymously}
                onCheckedChange={() => setFillAnonymously(!fillAnonymously)}
              />
              <Label htmlFor="fillAnonymously">{t("fillAnonymously")}</Label>
            </div>
            <Button
              type="submit"
              className="min-w-40"
              disabled={createSubmission.isPending}
            >
              {createSubmission.isPending ? <Spinner /> : <ICONS.save />}
              {t("submit")}
            </Button>
          </footer>
        </form>
      )}

      {createSubmission.isSuccess && (
        <Card className="flex flex-col gap-5 p-6 text-center">
          <ICONS.check className="text-primary border-primary mx-auto h-12 w-12 rounded-full border-2 p-2" />
          <h2 className="mb-2 text-xl font-medium">{t("submissionCreated")}</h2>
          <p>{formData.thanksMessage || t("defaultThanksMessage")}</p>
        </Card>
      )}

      {/* debugging */}
      {/* <pre>{JSON.stringify(form.watch(), null, 2)}</pre> */}
      {/* <pre>{JSON.stringify(form.formState.errors, null, 2)}</pre> */}
      {/* <pre>{JSON.stringify(formData, null, 2)}</pre> */}
    </section>
  );
};
