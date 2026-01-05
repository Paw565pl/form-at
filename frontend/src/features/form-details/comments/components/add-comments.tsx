import { Button } from "@/core/components/ui/button";
import { Input } from "@/core/components/ui/input";
import { ICONS } from "@/core/config/icons";
import { useCreateComment } from "@/features/form-details/comments/hooks/use-create-comment";
import {
  commentSchema,
  ErrorKey,
} from "@/features/form-details/comments/schemas/comment-schema";
import { zodResolver } from "@hookform/resolvers/zod";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { FieldError, useForm } from "react-hook-form";
import { z } from "zod";

interface AddCommentsProps {
  formIdOrSlug: string;
}

type CommentFormData = z.infer<typeof commentSchema>;

export const AddComments = ({ formIdOrSlug }: AddCommentsProps) => {
  const t = useTranslations("formDetailsPage.comments");
  const { data: session } = useSession();
  const { mutate: createComment, isPending } = useCreateComment({
    formIdOrSlug,
  });

  const getTranslatedErrors = (error?: FieldError): { message: string }[] => {
    return error ? [{ message: t(`${error.message as ErrorKey}`) }] : [];
  };

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CommentFormData>({
    resolver: zodResolver(commentSchema),
  });

  const onSubmit = (data: CommentFormData) => {
    createComment(data, {
      onSuccess: () => {
        reset();
      },
    });
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-1">
      <div className="flex items-center gap-1">
        <Input
          placeholder={session ? t("addComment") : t("loginToComment")}
          className="w-full"
          disabled={!session || isPending}
          autoComplete="off"
          {...register("content")}
        />
        <Button
          aria-label={t("addCommentButton")}
          variant="outline"
          disabled={!session || isPending}
          type="submit"
        >
          <ICONS.send />
        </Button>
      </div>
      {errors.content && (
        <p className="text-destructive text-sm">
          {getTranslatedErrors(errors.content)[0].message}
        </p>
      )}
    </form>
  );
};
