import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { getFetchFormCommentPagesQueryOptions } from "@/features/form-details/comments/hooks/use-fetch-form-comment-pages";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteCommentRating = (
  formIdOrSlug: string,
  commentId: string,
) =>
  useMutation<undefined, AxiosError<ErrorResponseDto>, undefined>({
    mutationKey: [
      "forms",
      formIdOrSlug,
      "comments",
      commentId,
      "rating",
      "delete",
    ],
    mutationFn: async () => {
      await apiService.delete<undefined>(
        `/api/v1/forms/${formIdOrSlug}/comments/${commentId}/rating`,
      );
    },
    onSuccess: (_, __, ___, { client }) => {
      client.invalidateQueries({
        queryKey: getFetchFormCommentPagesQueryOptions(formIdOrSlug).queryKey,
      });
    },
  });
