import { authenticatedApiService } from "@/core/services/api-service";
import { CommentRequestDto, CommentResponseDto } from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AxiosError } from "axios";

interface UseCreateCommentParams {
  formIdOrSlug: string;
}

export const useCreateComment = ({ formIdOrSlug }: UseCreateCommentParams) => {
  const queryClient = useQueryClient();

  const mutation = useMutation<
    CommentResponseDto,
    AxiosError<ErrorResponseDto> | Error,
    CommentRequestDto
  >({
    mutationKey: ["comments", formIdOrSlug, "create"] as const,
    mutationFn: async (request) => {
      const { data } = await authenticatedApiService.post(
        `/api/v1/forms/${formIdOrSlug}/comments`,
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
