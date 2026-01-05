import { getQueryClient } from "@/core/lib/tanstack-query";
import { authenticatedApiService } from "@/core/services/api-service";
import { CommentRequestDto, CommentResponseDto } from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { getFetchFormCommentsPagesQueryOptions } from "./use-fetch-form-comments-pages";

export const useEditComment = (formIdOrSlug: string, commentId: string) => {
  const queryClient = getQueryClient();

  const mutation = useMutation<
    CommentResponseDto,
    AxiosError<ErrorResponseDto>,
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
      queryClient.invalidateQueries({
        queryKey: getFetchFormCommentsPagesQueryOptions(formIdOrSlug).queryKey,
      });
    },
  });

  return mutation;
};
