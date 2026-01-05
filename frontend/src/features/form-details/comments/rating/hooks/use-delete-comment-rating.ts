import { getQueryClient } from "@/core/lib/tanstack-query";
import { authenticatedApiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { getFetchFormCommentsPagesQueryOptions } from "../../hooks/use-fetch-form-comments-pages";

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
      await authenticatedApiService.delete(
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
