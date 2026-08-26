import { apiService } from "@/core/services/api-service";
import {
  CommentRatingRequestDto,
  CommentRatingResponseDto,
} from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { getFetchFormCommentPagesQueryOptions } from "@/features/form-details/comments/hooks/use-fetch-form-comment-pages";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useCreateCommentRating = (
  formIdOrSlug: string,
  commentId: string,
) =>
  useMutation<
    CommentRatingResponseDto,
    AxiosError<ErrorResponseDto>,
    CommentRatingRequestDto
  >({
    mutationKey: [
      "forms",
      formIdOrSlug,
      "comments",
      commentId,
      "rating",
      "create",
    ],
    mutationFn: async (request) => {
      const { data } = await apiService.put<CommentRatingResponseDto>(
        `/api/v1/forms/${formIdOrSlug}/comments/${commentId}/rating`,
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
