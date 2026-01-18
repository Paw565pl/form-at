import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { SubmissionResponseDto } from "@/core/types/submission";
import { QueryClient, queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchMySubmissionOptions = (idOrSlug: string) =>
  queryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", idOrSlug, "submissions", "me"] as const,
    queryFn: async () => {
      const { data } = await apiService.get<SubmissionResponseDto>(
        `/api/v1/forms/${idOrSlug}/submissions/me`,
      );
      return data;
    },
    staleTime: 1000 * 60 * 10, // 10 minutes
  });

export const useFetchMySubmission = (idOrSlug: string) =>
  useQuery(getFetchMySubmissionOptions(idOrSlug));

export const prefetchMySubmission = (
  queryClient: QueryClient,
  idOrSlug: string,
) => queryClient.prefetchQuery(getFetchMySubmissionOptions(idOrSlug));
