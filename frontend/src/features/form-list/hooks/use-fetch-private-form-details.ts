import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormDetailResponseDto } from "@/core/types/form-detail-response-dto";
import { queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

const getFetchPrivateFormDetailsQueryOptions = (
  idOrSlug: string,
  password: string,
) =>
  queryOptions<FormDetailResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", idOrSlug, "access", password],
    queryFn: async () => {
      const { data } = await apiService.post<FormDetailResponseDto>(
        `/api/v1/forms/${idOrSlug}/access`,
        { password },
      );
      return data;
    },
  });

export const useFetchPrivateFormDetails = (
  idOrSlug: string,
  password: string,
) => useQuery(getFetchPrivateFormDetailsQueryOptions(idOrSlug, password));
