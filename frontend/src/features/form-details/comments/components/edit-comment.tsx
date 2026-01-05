import { Button } from "@/core/components/ui/button";
import { Spinner } from "@/core/components/ui/spinner";
import { Textarea } from "@/core/components/ui/textarea";
import { ICONS } from "@/core/config/icons";
import { getTranslatedErrors } from "@/core/utils/getTranslatedErrors";
import { useEditComment } from "@/features/form-details/comments/hooks/use-edit-comment";
import { commentSchema } from "@/features/form-details/comments/schemas/comment-schema";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslations } from "next-intl";
import { Controller, useForm } from "react-hook-form";
import { z } from "zod";

interface EditCommentProps {
  formIdOrSlug: string;
  commentId: string;
  initialContent: string;
  onSuccess: () => void;
  onCancel: () => void;
}

type CommentFormData = z.infer<typeof commentSchema>;

export const EditComment = ({
  formIdOrSlug,
  commentId,
  initialContent,
  onSuccess,
  onCancel,
}: EditCommentProps) => {
  const t = useTranslations("formDetailsPage.comments");
  const { mutate: editComment, isPending } = useEditComment({
    formIdOrSlug,
    commentId,
  });

  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<CommentFormData>({
    resolver: zodResolver(commentSchema),
    defaultValues: {
      content: initialContent,
    },
  });

  const onSubmit = (data: CommentFormData) => {
    editComment(data, {
      onSuccess: () => {
        onSuccess();
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
        render={({ field }) => (
          <div className="flex flex-col gap-1">
            <Textarea
              {...field}
              placeholder={t("add")}
              disabled={isPending}
              autoComplete="off"
            />
          </div>
        )}
      />
      {errors.content && (
        <p className="text-destructive text-sm">
          {getTranslatedErrors(t, errors.content)[0].message}
        </p>
      )}
      <div className="flex gap-2">
        <Button type="submit" disabled={isPending} variant="default">
          {isPending ? <Spinner /> : <ICONS.check />}
          {t("edit")}
        </Button>
        <Button
          type="button"
          onClick={onCancel}
          disabled={isPending}
          variant="outline"
        >
          {t("cancel")}
        </Button>
      </div>
    </form>
  );
};
