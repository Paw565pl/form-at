import { getQueryClient } from "@/core/lib/tanstack-query";
import { authenticatedApiService } from "@/core/services/api-service";
import {
  CommentRatingRequestDto,
  CommentRatingResponseDto,
} from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { getFetchFormCommentsPagesQueryOptions } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

interface UseCreateCommentRatingParams {
  formIdOrSlug: string;
  commentId: string;
}

export const useCreateCommentRating = ({
  formIdOrSlug,
  commentId,
}: UseCreateCommentRatingParams) => {
  const queryClient = getQueryClient();

  const mutation = useMutation<
    CommentRatingResponseDto,
    AxiosError<ErrorResponseDto>,
    CommentRatingRequestDto
  >({
    mutationKey: [
      "comments",
      formIdOrSlug,
      commentId,
      "rating",
      "create",
    ] as const,
    mutationFn: async (request) => {
      const { data } = await authenticatedApiService.post(
        `/api/v1/forms/${formIdOrSlug}/comments/${commentId}/rating`,
        request,
      );

      return data;
    },
    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: getFetchFormCommentsPagesQueryOptions(formIdOrSlug).queryKey,
      });
    },
  });

  return mutation;
};
