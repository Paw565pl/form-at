import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { SubmissionResponseDto } from "@/core/types/submission";
import { QueryClient, queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchSubmissionDetailsQueryOptions = (
  idOrSlug: string,
  submissionId: string,
) =>
  queryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", idOrSlug, "submissions", submissionId] as const,
    queryFn: async () => {
      const { data } = await apiService.get<SubmissionResponseDto>(
        `/api/v1/forms/${idOrSlug}/submissions/${submissionId}`,
      );
      return data;
    },
    staleTime: 1000 * 60 * 10, // 10 minutes
  });

export const useFetchSubmissionDetails = (
  idOrSlug: string,
  submissionId: string,
) => useQuery(getFetchSubmissionDetailsQueryOptions(idOrSlug, submissionId));

export const prefetchSubmissionDetails = (
  queryClient: QueryClient,
  idOrSlug: string,
  submissionId: string,
) =>
  queryClient.prefetchQuery(
    getFetchSubmissionDetailsQueryOptions(idOrSlug, submissionId),
  );
