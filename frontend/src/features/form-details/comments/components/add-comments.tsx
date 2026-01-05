import { Button } from "@/core/components/ui/button";
import { Input } from "@/core/components/ui/input";
import { ICONS } from "@/core/config/icons";
import { getTranslatedErrors } from "@/core/utils/getTranslatedErrors";
import { useCreateComment } from "@/features/form-details/comments/hooks/use-create-comment";
import { commentSchema } from "@/features/form-details/comments/schemas/comment-schema";
import { zodResolver } from "@hookform/resolvers/zod";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import { Controller, useForm } from "react-hook-form";
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

  const {
    handleSubmit,
    reset,
    control,
    formState: { errors },
  } = useForm<CommentFormData>({
    resolver: zodResolver(commentSchema),
    defaultValues: {
      content: "",
    },
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
        <Controller
          name="content"
          control={control}
          render={({ field }) => (
            <div className="flex w-full items-center gap-1">
              <Input
                {...field}
                placeholder={session ? t("add") : t("loginToComment")}
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
          )}
        />
      </div>
      {errors.content && (
        <p className="text-destructive text-sm">
          {getTranslatedErrors(t, errors.content)[0].message}
        </p>
      )}
    </form>
  );
};
