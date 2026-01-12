"use client";

import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { Checkbox } from "@/core/components/ui/checkbox";
import { Field, FieldError, FieldLabel } from "@/core/components/ui/field";
import { Label } from "@/core/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/core/components/ui/radio-group";
import { Spinner } from "@/core/components/ui/spinner";
import { Textarea } from "@/core/components/ui/textarea";
import { ICONS } from "@/core/config/icons";
import { FormImageWithFallback } from "@/core/form-image/form-image-with-fallback";
import { cn } from "@/core/lib/cn";
import { FormDetailResponseDto } from "@/core/types/form";
import {
  SubmissionAnswerRequestDto,
  SubmissionRequestDto,
} from "@/core/types/submission";
import { useCreateSubmission } from "@/features/submission-create/hooks/use-create-submission";
import { getSubmissionSchema } from "@/features/submission-create/schemas/submission-schema";
import { zodResolver } from "@hookform/resolvers/zod";
import { HttpStatusCode } from "axios";
import { useTranslations } from "next-intl";
import { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { toast } from "sonner";
import z from "zod";

type FormData = z.infer<ReturnType<typeof getSubmissionSchema>>;

interface SubmissionProps {
  readonly formData: FormDetailResponseDto;
}

export const Submission = ({ formData }: SubmissionProps) => {
  const t = useTranslations("submissionCreatePage");
  const gt = useTranslations("global");
  const createSubmission = useCreateSubmission(formData.id);
  const [isSubmissionComplete, setIsSubmissionComplete] = useState(false);

  const formSchema = getSubmissionSchema(t);
  const form = useForm<FormData>({
    resolver: zodResolver(formSchema),
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

  // manually trigger validation if locale has changed
  useEffect(() => {
    if (Object.keys(form.formState.errors).length > 0) form.trigger();
  }, [form, t]);

  const onSubmit = (data: FormData) => {
    const request: SubmissionRequestDto = {
      ...data,
      answers: data.answers
        .filter(
          // filter out empty answers for non-required questions
          (answer: SubmissionAnswerRequestDto) =>
            answer.chosenAnswerIds.length > 0 || answer.openAnswer !== "",
        )
        .map((answer: SubmissionAnswerRequestDto) => ({
          questionId: answer.questionId,
          chosenAnswerIds: answer.chosenAnswerIds,
          openAnswer: answer.openAnswer || null,
        })),
    };

    if (formData.saveSubmissions) {
      createSubmission.mutate(request, {
        onError: (error) => {
          if (error.status === HttpStatusCode.Conflict) {
            toast.error(t("errors.submissionExists"));
          } else {
            toast.error(
              error.response?.data?.message || t("errors.unexpected"),
            );
          }
        },
        onSuccess: () => {
          setIsSubmissionComplete(true);
        },
      });
    } else {
      setIsSubmissionComplete(true);
    }
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

      {!isSubmissionComplete && (
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="flex flex-col gap-4"
        >
          {formData.questions.map((question, index) => (
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

                <div className="flex flex-col flex-wrap gap-1 sm:flex-row sm:items-center sm:justify-between">
                  <div className="flex gap-1">
                    <span className="text-muted-foreground">{index + 1}.</span>
                    <h2 className="font-medium">
                      {question.content}{" "}
                      {question.isRequired && (
                        <span className="text-muted-foreground">*</span>
                      )}
                    </h2>
                  </div>
                  <span className="text-muted-foreground ml-3 text-sm">
                    {gt(`questionTypes.${question.type}`)}
                  </span>
                </div>
              </header>

              {question.type === "OPEN" && (
                <Controller
                  name={`answers.${index}.openAnswer`}
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel
                        htmlFor={`answers.${index}.openAnswer`}
                        className="text-muted-foreground ml-3"
                      >
                        {t("yourAnswer")}
                      </FieldLabel>
                      <Textarea
                        {...field}
                        id={`answers.${index}.openAnswer`}
                        aria-invalid={fieldState.invalid}
                        autoComplete="off"
                        className="max-h-100 min-h-24"
                        disabled={createSubmission.isPending}
                        maxLength={1000}
                      />
                      {fieldState.invalid && (
                        <FieldError
                          className="mx-3 max-w-fit"
                          errors={[fieldState.error]}
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
                            <RadioGroupItem value={answer.id} id={answer.id} />
                            <Label
                              htmlFor={answer.id}
                              className={cn(
                                createSubmission.isPending &&
                                  "text-muted-foreground cursor-not-allowed",
                              )}
                            >
                              {answer.content}
                            </Label>
                          </div>
                        ))}
                      </RadioGroup>
                      {fieldState.invalid && (
                        <FieldError
                          className="mx-3 max-w-fit"
                          errors={[fieldState.error]}
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
                            <Label htmlFor={answer.id}>{answer.content}</Label>
                          </div>
                        ))}
                      </div>
                      {fieldState.invalid && (
                        <FieldError
                          className="mx-3 max-w-fit"
                          errors={[fieldState.error]}
                        />
                      )}
                    </Field>
                  )}
                />
              )}
            </Card>
          ))}

          <footer className="flex justify-end gap-4">
            <Button
              type="submit"
              className="min-w-40"
              disabled={
                createSubmission.isPending || createSubmission.isSuccess
              }
            >
              {createSubmission.isPending ? <Spinner /> : <ICONS.save />}
              {t("submit")}
            </Button>
          </footer>
        </form>
      )}

      {isSubmissionComplete && (
        <Card className="flex flex-col gap-5 p-6 text-center">
          <ICONS.check className="text-primary border-primary mx-auto h-12 w-12 rounded-full border-2 p-2" />
          <h2 className="mb-2 text-xl font-medium">{t("submissionCreated")}</h2>
          <p>{formData.thanksMessage || t("defaultThanksMessage")}</p>
        </Card>
      )}
    </section>
  );
};
