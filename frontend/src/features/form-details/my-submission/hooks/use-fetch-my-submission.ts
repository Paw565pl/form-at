import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { SubmissionResponseDto } from "@/core/types/submission";
import { QueryClient, queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchMySubmissionOptions = (formIdOrSlug: string) =>
  queryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", formIdOrSlug, "submissions", "me"] as const,
    queryFn: async () => {
      const { data } = await apiService.get<SubmissionResponseDto>(
        `/api/v1/forms/${formIdOrSlug}/submissions/me`,
      );
      return data;
    },
    staleTime: 1000 * 60 * 10, // 10 minutes
  });

export const useFetchMySubmission = (formIdOrSlug: string) =>
  useQuery(getFetchMySubmissionOptions(formIdOrSlug));

export const prefetchMySubmission = (
  queryClient: QueryClient,
  formIdOrSlug: string,
) => queryClient.prefetchQuery(getFetchMySubmissionOptions(formIdOrSlug));
