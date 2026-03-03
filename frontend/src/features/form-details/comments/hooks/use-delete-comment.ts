import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { getFetchFormCommentsPagesQueryOptions } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteComment = (formIdOrSlug: string, commentId: string) =>
  useMutation<undefined, AxiosError<ErrorResponseDto>, undefined>({
    mutationKey: ["forms", formIdOrSlug, "comments", commentId, "delete"],
    mutationFn: async () => {
      await apiService.delete<undefined>(
        `/api/v1/forms/${formIdOrSlug}/comments/${commentId}`,
      );
    },
    onSuccess: (_, __, ___, { client }) => {
      client.invalidateQueries({
        queryKey: getFetchFormCommentsPagesQueryOptions(formIdOrSlug).queryKey,
      });
    },
  });
