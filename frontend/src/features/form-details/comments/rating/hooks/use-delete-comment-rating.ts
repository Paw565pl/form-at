import { getQueryClient } from "@/core/lib/tanstack-query";
import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { getFetchFormCommentsPagesQueryOptions } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

interface UseDeleteCommentRatingParams {
  readonly formIdOrSlug: string;
  readonly commentId: string;
}

export const useDeleteCommentRating = ({
  formIdOrSlug,
  commentId,
}: UseDeleteCommentRatingParams) => {
  const queryClient = getQueryClient();

  const mutation = useMutation<undefined, AxiosError<ErrorResponseDto>>({
    mutationKey: [
      "comments",
      formIdOrSlug,
      commentId,
      "rating",
      "delete",
    ] as const,
    mutationFn: async () => {
      await apiService.delete(
        `/api/v1/forms/${formIdOrSlug}/comments/${commentId}/rating`,
      );
    },
    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: getFetchFormCommentsPagesQueryOptions(formIdOrSlug).queryKey,
      });
    },
  });

  return mutation;
};
