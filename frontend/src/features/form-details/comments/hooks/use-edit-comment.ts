import { authenticatedApiService } from "@/core/services/api-service";
import { CommentRequestDto, CommentResponseDto } from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AxiosError } from "axios";

interface UseEditCommentParams {
  formIdOrSlug: string;
  commentId: string;
}

export const useEditComment = ({
  formIdOrSlug,
  commentId,
}: UseEditCommentParams) => {
  const queryClient = useQueryClient();

  const mutation = useMutation<
    CommentResponseDto,
    AxiosError<ErrorResponseDto> | Error,
    CommentRequestDto
  >({
    mutationKey: ["comments", formIdOrSlug, "update", commentId] as const,
    mutationFn: async (request) => {
      const { data } = await authenticatedApiService.put(
        `/api/v1/forms/${formIdOrSlug}/comments/${commentId}`,
        request,
      );

      return data;
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: [formIdOrSlug, "comments"] });
    },
  });

  return { ...mutation };
};
