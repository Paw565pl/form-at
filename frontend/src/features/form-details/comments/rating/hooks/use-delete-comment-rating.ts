import { authenticatedApiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AxiosError } from "axios";

interface UseDeleteCommentRatingParams {
  formIdOrSlug: string;
  commentId: string;
}

export const useDeleteCommentRating = ({
  formIdOrSlug,
  commentId,
}: UseDeleteCommentRatingParams) => {
  const queryClient = useQueryClient();

  const mutation = useMutation<void, AxiosError<ErrorResponseDto> | Error>({
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
        queryKey: [formIdOrSlug, "comments"],
      });
    },
  });

  return { ...mutation };
};
