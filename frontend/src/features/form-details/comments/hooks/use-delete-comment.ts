import { authenticatedApiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AxiosError } from "axios";

interface UseDeleteCommentParams {
  formIdOrSlug: string;
  commentId: string;
}

export const useDeleteComment = ({
  formIdOrSlug,
  commentId,
}: UseDeleteCommentParams) => {
  const queryClient = useQueryClient();

  const mutation = useMutation<undefined, AxiosError<ErrorResponseDto> | Error>(
    {
      mutationKey: ["comments", formIdOrSlug, commentId, "delete"] as const,
      mutationFn: async () => {
        await authenticatedApiService.delete(
          `/api/v1/forms/${formIdOrSlug}/comments/${commentId}`,
        );
      },
      onSettled: () => {
        queryClient.invalidateQueries({
          queryKey: [formIdOrSlug, "comments"],
        });
      },
    },
  );

  return { ...mutation };
};
