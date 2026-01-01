import { authenticatedApiService } from "@/core/services/api-service";
import {
  CommentRatingRequestDto,
  CommentRatingResponseDto,
} from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AxiosError } from "axios";

interface UseCreateCommentRatingParams {
  formIdOrSlug: string;
  commentId: string;
}

export const useCreateCommentRating = ({
  formIdOrSlug,
  commentId,
}: UseCreateCommentRatingParams) => {
  const queryClient = useQueryClient();

  const mutation = useMutation<
    CommentRatingResponseDto,
    AxiosError<ErrorResponseDto> | Error,
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
        queryKey: [formIdOrSlug, "comments"],
      });
    },
  });

  return { ...mutation };
};
