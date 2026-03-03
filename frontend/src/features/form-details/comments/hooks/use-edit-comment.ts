import { apiService } from "@/core/services/api-service";
import { CommentRequestDto, CommentResponseDto } from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { getFetchFormCommentsPagesQueryOptions } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useEditComment = (formIdOrSlug: string, commentId: string) =>
  useMutation<
    CommentResponseDto,
    AxiosError<ErrorResponseDto>,
    CommentRequestDto
  >({
    mutationKey: ["forms", formIdOrSlug, "comments", commentId, "update"],
    mutationFn: async (request) => {
      const { data } = await apiService.put<CommentResponseDto>(
        `/api/v1/forms/${formIdOrSlug}/comments/${commentId}`,
        request,
      );
      return data;
    },
    onSuccess: (_, __, ___, { client }) => {
      client.invalidateQueries({
        queryKey: getFetchFormCommentsPagesQueryOptions(formIdOrSlug).queryKey,
      });
    },
  });
