import { Button } from "@/core/components/ui/button";
import { Field, FieldError } from "@/core/components/ui/field";
import { Input } from "@/core/components/ui/input";
import { ICONS } from "@/core/config/icons";
import { useCreateComment } from "@/features/form-details/comments/hooks/use-create-comment";
import { getCommentSchema } from "@/features/form-details/comments/schemas/comment-schema";
import { zodResolver } from "@hookform/resolvers/zod";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

type CommentFormData = z.infer<ReturnType<typeof getCommentSchema>>;

interface CreateCommentFormProps {
  readonly formIdOrSlug: string;
}

export const CreateCommentForm = ({ formIdOrSlug }: CreateCommentFormProps) => {
  const t = useTranslations("formDetailsPage.comments");

  const { data: session } = useSession();
  const { mutate: createComment, isPending } = useCreateComment(formIdOrSlug);

  const commentSchema = getCommentSchema(t);
  const form = useForm<CommentFormData>({
    resolver: zodResolver(commentSchema),
    defaultValues: {
      content: "",
    },
  });

  // manually trigger validation if locale has changed
  useEffect(() => {
    if (Object.keys(form.formState.errors).length > 0) form.trigger();
  }, [form, t]);

  const onSubmit = (data: CommentFormData) => {
    createComment(data, {
      onSuccess: () => {
        toast.success(t("addSuccess"));
        form.reset();
      },
      onError: () => {
        toast.error(t("addError"));
      },
    });
  };

  return (
    <form
      onSubmit={form.handleSubmit(onSubmit)}
      className="flex flex-col gap-1"
    >
      <div className="flex items-center gap-1">
        <Controller
          name="content"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <div className="flex w-full items-center gap-1">
                <Input
                  {...field}
                  placeholder={session ? t("add") : t("loginToComment")}
                  aria-invalid={fieldState.invalid}
                  disabled={!session || isPending}
                  autoComplete="off"
                />
                <Button
                  aria-label={t("addButton")}
                  variant="outline"
                  disabled={!session || isPending}
                  type="submit"
                >
                  <ICONS.send />
                </Button>
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
      </div>
    </form>
  );
};
