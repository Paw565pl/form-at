"use client";

import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { Checkbox } from "@/core/components/ui/checkbox";
import { Field, FieldError, FieldLabel } from "@/core/components/ui/field";
import { Input } from "@/core/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/core/components/ui/select";
import { Textarea } from "@/core/components/ui/textarea";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import {
  FormEstimatedDuration,
  FormRequest,
  FormShuffleVariant,
  FormStatus,
  Language,
} from "@/core/types/form";
import { QuestionType } from "@/core/types/question";
import { useCreateForm } from "@/features/form-create/hooks/use-create-form";
import {
  ErrorKey,
  formSchema,
} from "@/features/form-create/schemas/form-schemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useState } from "react";
import type { FieldError as FieldErrorType } from "react-hook-form";
import { Controller, useFieldArray, useForm } from "react-hook-form";
import { toast } from "sonner";
import z from "zod";

const languageOptions: { label: string; value: Language }[] = [
  { label: "English", value: Language.En },
  { label: "Polski", value: Language.Pl },
] as const;

// TODO
// Translating errors
// Weird behavior of questions array errors showing
// Cleanup

export const FormCreatePage = () => {
  const t = useTranslations("formCreatePage");
  const router = useRouter();
  const createForm = useCreateForm();
  const [showQuestions, setShowQuestions] = useState<boolean>(true);

  const getTranslatedErrors = (
    error?: FieldErrorType,
  ): { message: string }[] => {
    return error ? [{ message: t(`errors.${error.message as ErrorKey}`) }] : [];
  };

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: "",
      description: "",
      language: Language.En,
      status: FormStatus.Public,
      shuffleVariant: "NONE",
      password: "",
      thanksMessage: "",
      estimatedDuration: FormEstimatedDuration.PT10M,
      allowsQuestionsPreview: false,
      allowsGuestSubmissions: false,
      saveSubmissions: true,
      thumbnail: undefined,
      questions: [
        {
          content: "",
          type: QuestionType.Open,
          isRequired: true,
          answers: [],
        },
      ],
    },
  });

  // eslint-disable-next-line react-hooks/incompatible-library
  const watchedQuestions = form.watch("questions") || [];

  const {
    fields: questions,
    append: appendQuestion,
    remove: removeQuestion,
  } = useFieldArray({
    control: form.control,
    name: "questions",
  });

  const appendAnswer = (questionIdx: number) => {
    const currentAnswers =
      form.getValues(`questions.${questionIdx}.answers`) || [];
    form.setValue(`questions.${questionIdx}.answers`, [
      ...currentAnswers,
      { content: "", isCorrect: false },
    ]);
  };

  const removeAnswer = (questionIdx: number, answerIdx: number) => {
    const currentAnswers =
      form.getValues(`questions.${questionIdx}.answers`) || [];
    form.setValue(
      `questions.${questionIdx}.answers`,
      currentAnswers.filter((_, idx) => idx !== answerIdx),
    );
  };

  function onSubmit(data: z.infer<typeof formSchema>) {
    const request: FormRequest = {
      ...data,
      name: data.name.trim(),
      description: data.description.trim() === "" ? null : data.description,
      password: data.status === FormStatus.Private ? data.password : null,
      thanksMessage:
        data.thanksMessage.trim() === "" ? null : data.thanksMessage,
      shuffleVariant:
        data.shuffleVariant === "NONE" ? null : data.shuffleVariant,
      thumbnail: data.thumbnail || null,
      questions: data.questions.map((q) => {
        return {
          ...q,
          image: q.image || null,
        };
      }),
    };

    createForm.mutate(request, {
      onSuccess: (data) => {
        router.push(`/forms/${data.slug}`);
      },
      onError: (error) => {
        toast.error(error?.response?.data?.message || t("errors.unexpected"));
      },
    });
  }

  return (
    <section
      id="form-create"
      className="flex w-full flex-col gap-4 px-5 py-10 lg:px-30"
    >
      <h1 className="ml-4 text-xl font-bold">{t("pageTitle")}</h1>

      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className="flex flex-col gap-4"
      >
        <div className="flex flex-col gap-4 md:grid md:grid-cols-3">
          <Card className="col-span-2 gap-4 p-4">
            <Controller
              name="name"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel
                    className="items-end justify-between px-3"
                    htmlFor="name"
                  >
                    {t("name")}
                    <span className="text-muted-foreground text-xs">
                      {field.value ? field.value.length : 0}/
                      {formSchema.shape.name.maxLength}
                    </span>
                  </FieldLabel>

                  <Input
                    {...field}
                    id="name"
                    aria-invalid={fieldState.invalid}
                    placeholder={t("namePlaceholder")}
                    autoComplete="off"
                  />
                  {fieldState.invalid && (
                    <FieldError
                      className="mx-3 max-w-fit"
                      errors={getTranslatedErrors(fieldState.error)}
                    />
                  )}
                </Field>
              )}
            />

            <Controller
              name="description"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel
                    className="items-end justify-between px-3"
                    htmlFor="description"
                  >
                    {t("description")}
                    <span className="text-muted-foreground text-xs">
                      {field.value ? field.value.length : 0}/2000
                    </span>
                  </FieldLabel>
                  <Textarea
                    {...field}
                    id="description"
                    aria-invalid={fieldState.invalid}
                    autoComplete="off"
                    className="min-h-24"
                  />
                  {fieldState.invalid && (
                    <FieldError
                      className="mx-3 max-w-fit"
                      errors={getTranslatedErrors(fieldState.error)}
                    />
                  )}
                </Field>
              )}
            />

            <Controller
              name="status"
              control={form.control}
              render={({ field }) => (
                <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
                  <Select
                    name={field.name}
                    onValueChange={(value) => {
                      field.onChange(value);
                      if (value !== FormStatus.Private) {
                        form.setValue("password", "");
                      }
                    }}
                    value={field.value}
                  >
                    <SelectTrigger
                      aria-label="Status"
                      className="w-full sm:w-auto"
                    >
                      <span className="flex-1 text-left">Status:</span>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.values(FormStatus).map((status) => (
                        <SelectItem key={status} value={status}>
                          {t(`formStatuses.${status}`)}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <p className="ml-3 text-sm sm:ml-0">
                    {/* eslint-disable-next-line */}
                    {t(`formStatuses.${field.value!}Desc`)}
                  </p>
                </div>
              )}
            />

            {form.watch("status") === FormStatus.Private && (
              <Controller
                name="password"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <Input
                      {...field}
                      id="password"
                      aria-invalid={fieldState.invalid}
                      type="password"
                      autoComplete="off"
                      placeholder={t("password")}
                    />
                    {fieldState.invalid && (
                      <FieldError
                        className="mx-3 max-w-fit"
                        errors={getTranslatedErrors(fieldState.error)}
                      />
                    )}
                  </Field>
                )}
              />
            )}

            <Controller
              name="thanksMessage"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel
                    className="items-end justify-between px-3"
                    htmlFor="thanksMessage"
                  >
                    {t("thanksMessage")}
                    <span className="text-muted-foreground text-xs">
                      {field.value ? field.value.length : 0}/500
                    </span>
                  </FieldLabel>
                  <Textarea
                    {...field}
                    id="thanksMessage"
                    aria-invalid={fieldState.invalid}
                    autoComplete="off"
                    className="min-h-24"
                  />
                  {fieldState.invalid && (
                    <FieldError
                      className="mx-3 max-w-fit"
                      errors={getTranslatedErrors(fieldState.error)}
                    />
                  )}
                </Field>
              )}
            />
          </Card>

          <Card className="gap-4 p-4">
            <Controller
              name="thumbnail"
              control={form.control}
              render={({
                // eslint-disable-next-line
                field: { value, onChange, ...fieldProps },
                fieldState,
              }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel
                    className="items-end justify-between px-3"
                    htmlFor="thumbnail"
                  >
                    {t("thumbnailImage")}
                    <span className="text-muted-foreground text-xs">
                      10MB max
                    </span>
                  </FieldLabel>
                  <Input
                    {...fieldProps}
                    id="thumbnail"
                    aria-invalid={fieldState.invalid}
                    type="file"
                    onChange={(event) =>
                      onChange(event.target.files && event.target.files[0])
                    }
                  />
                  {fieldState.invalid && (
                    <FieldError
                      className="mx-3 max-w-fit"
                      errors={getTranslatedErrors(fieldState.error)}
                    />
                  )}
                </Field>
              )}
            />

            <Controller
              name="language"
              control={form.control}
              render={({ field }) => (
                <Select
                  name={field.name}
                  onValueChange={field.onChange}
                  value={field.value}
                >
                  <SelectTrigger className="w-full" aria-label={t("language")}>
                    <span className="flex-1 text-left">{t("language")}:</span>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {languageOptions.map((lang) => (
                      <SelectItem key={lang.value} value={lang.value}>
                        {lang.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />

            <Controller
              name="shuffleVariant"
              control={form.control}
              render={({ field }) => (
                <Select
                  name={field.name}
                  onValueChange={field.onChange}
                  value={field.value}
                >
                  <SelectTrigger className="w-full" aria-label={t("shuffle")}>
                    <span className="flex-1 text-left">{t("shuffle")}:</span>
                    <SelectValue className="text-primary" />
                  </SelectTrigger>
                  <SelectContent>
                    {Object.values(FormShuffleVariant).map((variant) => (
                      <SelectItem key={variant} value={variant}>
                        {t(`shuffleOptions.${variant}`)}
                      </SelectItem>
                    ))}
                    <SelectItem key={"NONE"} value={"NONE"}>
                      {t(`shuffleOptions.NONE`)}
                    </SelectItem>
                  </SelectContent>
                </Select>
              )}
            />

            <Controller
              name="estimatedDuration"
              control={form.control}
              render={({ field }) => (
                <Select
                  name={field.name}
                  onValueChange={field.onChange}
                  value={field.value}
                >
                  <SelectTrigger
                    className="w-full"
                    aria-label={t("estimatedDuration")}
                  >
                    <span className="flex-1 text-left">
                      {t("estimatedDuration")}:
                    </span>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {Object.values(FormEstimatedDuration).map((option) => (
                      <SelectItem key={option} value={option}>
                        {t(`durationOptions.${option}`)}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />

            <Controller
              name="allowsQuestionsPreview"
              control={form.control}
              render={({ field }) => (
                <Field className="flex-row items-center justify-between px-3">
                  <FieldLabel htmlFor="allowsQuestionsPreview">
                    {t("allowsQuestionsPreview")}
                  </FieldLabel>
                  <Checkbox
                    id="allowsQuestionsPreview"
                    className="max-w-5"
                    checked={field.value}
                    onCheckedChange={field.onChange}
                  />
                </Field>
              )}
            />

            <Controller
              name="allowsGuestSubmissions"
              control={form.control}
              render={({ field }) => (
                <Field className="flex-row items-center justify-between px-3">
                  <FieldLabel htmlFor="allowsGuestSubmissions">
                    {t("allowsGuestSubmissions")}
                  </FieldLabel>
                  <Checkbox
                    id="allowsGuestSubmissions"
                    className="max-w-5"
                    checked={field.value}
                    onCheckedChange={field.onChange}
                  />
                </Field>
              )}
            />

            <Controller
              name="saveSubmissions"
              control={form.control}
              render={({ field }) => (
                <Field className="flex-row items-center justify-between px-3">
                  <FieldLabel htmlFor="saveSubmissions">
                    {t("saveSubmissions")}
                  </FieldLabel>
                  <Checkbox
                    id="saveSubmissions"
                    className="max-w-5"
                    checked={field.value}
                    onCheckedChange={field.onChange}
                  />
                </Field>
              )}
            />
          </Card>
        </div>

        <header className="flex items-center justify-between">
          <h1 className="ml-4 text-lg font-bold">
            {t("questions")}{" "}
            {questions.length > 0 ? `(${questions.length})` : ""}
          </h1>

          <div className="flex gap-2">
            <Button
              type="button"
              onClick={() =>
                appendQuestion({
                  content: "",
                  type: QuestionType.Open,
                  isRequired: true,
                  answers: [],
                })
              }
              size="sm"
            >
              <ICONS.add />
              {t("addQuestion")}
            </Button>

            <Tooltip>
              <TooltipTrigger asChild>
                <Button
                  variant="outline"
                  size="icon-sm"
                  type="button"
                  onClick={() => setShowQuestions(!showQuestions)}
                  aria-label={
                    showQuestions ? t("hideQuestions") : t("showQuestions")
                  }
                >
                  <ICONS.expandDown
                    className={`transition-transform ${showQuestions ? "rotate-180" : ""}`}
                  />
                </Button>
              </TooltipTrigger>
              <TooltipContent>
                <span>
                  {showQuestions ? t("hideQuestions") : t("showQuestions")}
                </span>
              </TooltipContent>
            </Tooltip>
          </div>
        </header>

        {showQuestions &&
          questions.map((question, qIdx) => {
            const answers = watchedQuestions[qIdx]?.answers || [];

            return (
              <Card key={question.id} className="gap-4 p-4">
                <header className="flex items-center justify-between gap-8">
                  <h2 className="ml-3 font-semibold">
                    {t("question")} {qIdx + 1}.
                  </h2>

                  <Tooltip>
                    <TooltipTrigger asChild>
                      <Button
                        type="button"
                        variant="destructive"
                        size="icon-sm"
                        disabled={questions.length <= 1}
                        aria-label={t("deleteQuestion")}
                        onClick={() => removeQuestion(qIdx)}
                      >
                        <ICONS.delete />
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>
                      <span>{t("deleteQuestion")}</span>
                    </TooltipContent>
                  </Tooltip>
                </header>

                <Controller
                  name={`questions.${qIdx}.content`}
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel
                        className="items-end justify-between px-3"
                        htmlFor={`questions.${qIdx}.content`}
                      >
                        {t("questionContent")}
                        <span className="text-muted-foreground text-xs">
                          {field.value ? field.value.length : 0}/200
                        </span>
                      </FieldLabel>
                      <Input
                        {...field}
                        id={`questions.${qIdx}.content`}
                        aria-invalid={fieldState.invalid}
                        placeholder={t("questionPlaceholder")}
                      />
                      {fieldState.invalid && (
                        <FieldError
                          className="mx-3 max-w-fit"
                          errors={getTranslatedErrors(fieldState.error)}
                        />
                      )}
                    </Field>
                  )}
                />

                <Controller
                  name={`questions.${qIdx}.image`}
                  control={form.control}
                  render={({
                    // eslint-disable-next-line
                    field: { value, onChange, ...fieldProps },
                    fieldState,
                  }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel
                        className="items-end justify-between px-3"
                        htmlFor="image"
                      >
                        {t("thumbnailImage")}
                        <span className="text-muted-foreground text-xs">
                          10MB max
                        </span>
                      </FieldLabel>
                      <Input
                        {...fieldProps}
                        id="image"
                        aria-invalid={fieldState.invalid}
                        type="file"
                        onChange={(event) =>
                          onChange(event.target.files && event.target.files[0])
                        }
                      />
                      {fieldState.invalid && (
                        <FieldError
                          className="mx-3 max-w-fit"
                          errors={getTranslatedErrors(fieldState.error)}
                        />
                      )}
                    </Field>
                  )}
                />

                <div className="flex w-full flex-col items-center justify-between gap-4 sm:flex-row">
                  <Controller
                    name={`questions.${qIdx}.type`}
                    control={form.control}
                    render={({ field }) => (
                      <Select
                        name={field.name}
                        onValueChange={(value) => {
                          field.onChange(value);
                          if (value === QuestionType.Open) {
                            form.setValue(`questions.${qIdx}.answers`, []);
                          }
                        }}
                        value={field.value}
                      >
                        <SelectTrigger
                          aria-label={t("type")}
                          className="w-full sm:w-auto"
                        >
                          {t("type")}:
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          {Object.values(QuestionType).map((type) => (
                            <SelectItem key={type} value={type}>
                              {t(`questionTypes.${type}`)}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    )}
                  />

                  <Controller
                    name={`questions.${qIdx}.isRequired`}
                    control={form.control}
                    render={({ field }) => (
                      <Field className="flex-row items-center gap-2">
                        <Checkbox
                          id={`questions.${qIdx}.isRequired`}
                          className="max-w-5"
                          checked={field.value}
                          onCheckedChange={field.onChange}
                        />
                        <FieldLabel htmlFor={`questions.${qIdx}.isRequired`}>
                          {t("required")}
                        </FieldLabel>
                      </Field>
                    )}
                  />
                </div>

                {watchedQuestions[qIdx]?.type != QuestionType.Open && (
                  <div className="flex flex-col gap-4">
                    <header className="flex items-center justify-end gap-2">
                      <h2 className="mr-auto ml-3 font-semibold">
                        {t("answers")}
                      </h2>
                      <Button
                        size="sm"
                        type="button"
                        onClick={() => {
                          appendAnswer(qIdx);
                        }}
                        disabled={answers.length >= 6}
                      >
                        <ICONS.add />
                        {t("addAnswer")}
                      </Button>
                    </header>

                    {answers.length > 0 && (
                      <div className="flex flex-col gap-4">
                        {answers.map((answer, aIdx) => (
                          <div key={aIdx} className="flex flex-col gap-2">
                            <header className="ml-3 flex items-end gap-8">
                              <span className="text-sm">
                                {t("answer")} {aIdx + 1}.
                              </span>

                              <Controller
                                name={`questions.${qIdx}.answers.${aIdx}.isCorrect`}
                                control={form.control}
                                render={({ field }) => (
                                  <Field className="w-20 flex-row items-center">
                                    <Checkbox
                                      id={`questions.${qIdx}.answers.${aIdx}.isCorrect`}
                                      className="max-w-5"
                                      checked={field.value}
                                      onCheckedChange={field.onChange}
                                    />
                                    <FieldLabel
                                      htmlFor={`questions.${qIdx}.answers.${aIdx}.isCorrect`}
                                    >
                                      {t("correct")}
                                    </FieldLabel>
                                  </Field>
                                )}
                              />

                              <Tooltip>
                                <TooltipTrigger asChild>
                                  <Button
                                    type="button"
                                    variant="destructive"
                                    size="icon-sm"
                                    className="ml-auto"
                                    disabled={answers.length <= 1}
                                    aria-label={t("deleteAnswer")}
                                    onClick={() => removeAnswer(qIdx, aIdx)}
                                  >
                                    <ICONS.delete />
                                  </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                  <span>{t("deleteAnswer")}</span>
                                </TooltipContent>
                              </Tooltip>
                            </header>

                            <Controller
                              name={`questions.${qIdx}.answers.${aIdx}.content`}
                              control={form.control}
                              render={({ field, fieldState }) => (
                                <Field data-invalid={fieldState.invalid}>
                                  <Input
                                    {...field}
                                    id={`questions.${qIdx}.answers.${aIdx}.content`}
                                    aria-invalid={fieldState.invalid}
                                    autoComplete="off"
                                  />

                                  {fieldState.invalid && (
                                    <FieldError
                                      className="mx-3 max-w-fit"
                                      errors={getTranslatedErrors(
                                        fieldState.error,
                                      )}
                                    />
                                  )}
                                </Field>
                              )}
                            />
                          </div>
                        ))}
                      </div>
                    )}

                    {form.formState.errors.questions?.[qIdx]?.answers && (
                      <FieldError
                        className="mx-3 max-w-fit"
                        // TODO: translate this
                        errors={[form.formState.errors.questions[qIdx].answers]}
                      />
                    )}
                    {form.formState.errors.questions?.[qIdx]?.answers?.root && (
                      <FieldError
                        className="mx-3 max-w-fit"
                        errors={getTranslatedErrors(
                          form.formState.errors.questions[qIdx].answers.root,
                        )}
                      />
                    )}
                  </div>
                )}
              </Card>
            );
          })}

        {form.formState.errors.questions && (
          <FieldError
            className="mx-4 max-w-fit"
            // TODO: translate this
            errors={[form.formState.errors.questions]}
          />
        )}

        {form.formState.errors.questions &&
          form.formState.errors.questions.root && (
            <FieldError
              className="mx-3 max-w-fit"
              errors={getTranslatedErrors(form.formState.errors.questions.root)}
            />
          )}

        <Button type="submit" className="ml-auto w-40">
          <ICONS.save />
          {t("submit")}
        </Button>

        {/* for debugging */}
        <pre>{JSON.stringify(form.formState.errors, null, 2)}</pre>
      </form>
    </section>
  );
};
