import { authenticatedApiService } from "@/core/services/api-service";
import { CommentRequestDto, CommentResponseDto } from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

interface UseCreateCommentParams {
  formIdOrSlug: string;
}

export const useCreateComment = ({ formIdOrSlug }: UseCreateCommentParams) => {
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
    onSettled: (_, __, ___, _____, { client }) => {
      client.invalidateQueries({ queryKey: ["comments", formIdOrSlug] });
    },
  });

  return { ...mutation };
};
