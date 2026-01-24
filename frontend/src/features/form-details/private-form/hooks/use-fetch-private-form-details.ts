import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormDetailResponseDto } from "@/core/types/form";
import { queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchPrivateFormDetailsQueryOptions = (
  idOrSlug: string,
  password: string,
) =>
  queryOptions<FormDetailResponseDto, AxiosError<ErrorResponseDto>>({
    // eslint-disable-next-line @tanstack/query/exhaustive-deps
    queryKey: ["forms", idOrSlug, "access"] as const,
    queryFn: async () => {
      const { data } = await apiService.post<FormDetailResponseDto>(
        `/api/v1/forms/${idOrSlug}/access`,
        { password },
      );
      return data;
    },
    staleTime: 1000 * 60 * 10, // 10 minutes
  });

export const useFetchPrivateFormDetails = (
  idOrSlug: string,
  password: string,
) => useQuery(getFetchPrivateFormDetailsQueryOptions(idOrSlug, password));
