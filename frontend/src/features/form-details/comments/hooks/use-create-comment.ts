import { apiService } from "@/core/services/api-service";
import { CommentRequestDto, CommentResponseDto } from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { getFetchFormCommentPagesQueryOptions } from "@/features/form-details/comments/hooks/use-fetch-form-comment-pages";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useCreateComment = (formIdOrSlug: string) =>
  useMutation<
    CommentResponseDto,
    AxiosError<ErrorResponseDto>,
    CommentRequestDto
  >({
    mutationKey: ["forms", formIdOrSlug, "comments", "create"],
    mutationFn: async (request) => {
      const { data } = await apiService.post<CommentResponseDto>(
        `/api/v1/forms/${formIdOrSlug}/comments`,
        request,
      );
      return data;
    },
    onSuccess: (_, __, ___, { client }) => {
      client.invalidateQueries({
        queryKey: getFetchFormCommentPagesQueryOptions(formIdOrSlug).queryKey,
      });
    },
  });
