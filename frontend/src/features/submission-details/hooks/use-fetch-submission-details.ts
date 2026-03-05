import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { SubmissionResponseDto } from "@/core/types/submission";
import { QueryClient, queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

const getFetchSubmissionDetailsQueryOptions = (
  formIdOrSlug: string,
  submissionId: string,
) =>
  queryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", formIdOrSlug, "submissions", submissionId],
    queryFn: async () => {
      const { data } = await apiService.get<SubmissionResponseDto>(
        `/api/v1/forms/${formIdOrSlug}/submissions/${submissionId}`,
      );
      return data;
    },
  });

export const useFetchSubmissionDetails = (
  formIdOrSlug: string,
  submissionId: string,
) =>
  useQuery(getFetchSubmissionDetailsQueryOptions(formIdOrSlug, submissionId));

export const prefetchSubmissionDetails = (
  queryClient: QueryClient,
  formIdOrSlug: string,
  submissionId: string,
) =>
  queryClient.prefetchQuery(
    getFetchSubmissionDetailsQueryOptions(formIdOrSlug, submissionId),
  );
