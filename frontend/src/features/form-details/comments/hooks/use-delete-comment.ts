import { getQueryClient } from "@/core/lib/tanstack-query";
import { authenticatedApiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { getFetchFormCommentsPagesQueryOptions } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteComment = (formIdOrSlug: string, commentId: string) => {
  const queryClient = getQueryClient();

  const mutation = useMutation<undefined, AxiosError<ErrorResponseDto>>({
    mutationKey: ["comments", formIdOrSlug, commentId, "delete"] as const,
    mutationFn: async () => {
      await authenticatedApiService.delete(
        `/api/v1/forms/${formIdOrSlug}/comments/${commentId}`,
      );
    },
    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: getFetchFormCommentsPagesQueryOptions(formIdOrSlug).queryKey,
      });
    },
  });

  return mutation;
};
