import { Button } from "@/core/components/ui/button";
import { Field, FieldError } from "@/core/components/ui/field";
import { Spinner } from "@/core/components/ui/spinner";
import { Textarea } from "@/core/components/ui/textarea";
import { ICONS } from "@/core/config/icons";
import { useEditComment } from "@/features/form-details/comments/hooks/use-edit-comment";
import { getCommentSchema } from "@/features/form-details/comments/schemas/comment-schema";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslations } from "next-intl";
import { useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

interface EditCommentProps {
  readonly formIdOrSlug: string;
  readonly commentId: string;
  readonly initialContent: string;
  readonly onSuccess: () => void;
  readonly onCancel: () => void;
}

type CommentFormData = z.infer<ReturnType<typeof getCommentSchema>>;

export const EditComment = ({
  formIdOrSlug,
  commentId,
  initialContent,
  onSuccess,
  onCancel,
}: EditCommentProps) => {
  const t = useTranslations("formDetailsPage.comments");
  const { mutate: editComment, isPending } = useEditComment(
    formIdOrSlug,
    commentId,
  );

  const commentSchema = getCommentSchema((e) => t(`errors.${e}`));
  const {
    handleSubmit,
    control,
    trigger,
    formState: { errors },
  } = useForm<CommentFormData>({
    resolver: zodResolver(commentSchema),
    defaultValues: {
      content: initialContent,
    },
  });

  // manually trigger validation if there are any errors and locale has changed
  useEffect(() => {
    if (Object.keys(errors).length > 0) trigger();
  }, [trigger, errors, t]);

  const onSubmit = (data: CommentFormData) => {
    editComment(data, {
      onSuccess: () => {
        toast.success(t("editSuccess"));
        onSuccess();
      },
      onError: () => {
        toast.error(t("editError"));
      },
    });
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="flex flex-col gap-2 py-2"
    >
      <Controller
        name="content"
        control={control}
        render={({ field, fieldState }) => (
          <Field data-invalid={fieldState.invalid}>
            <div className="flex flex-col gap-1">
              <Textarea
                {...field}
                placeholder={t("add")}
                aria-invalid={fieldState.invalid}
                disabled={isPending}
                autoComplete="off"
              />
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
      <div className="flex justify-end gap-2">
        <Button
          type="button"
          onClick={onCancel}
          disabled={isPending}
          variant="outline"
        >
          {t("cancel")}
        </Button>
        <Button type="submit" disabled={isPending} variant="default">
          {isPending ? <Spinner /> : <ICONS.save />}
          {t("save")}
        </Button>
      </div>
    </form>
  );
};
