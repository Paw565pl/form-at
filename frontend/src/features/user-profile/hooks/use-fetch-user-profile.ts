import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { UserProfileResponseDto } from "@/core/types/user-profile-response-dto";
import { QueryClient, queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchUserProfileQueryOptions = (username: string) =>
  queryOptions<UserProfileResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["users", username],
    queryFn: async () => {
      const { data } = await apiService.get<UserProfileResponseDto>(
        `/api/v1/users/${username}`,
      );
      return data;
    },
  });

export const useFetchUserProfile = (username: string) =>
  useQuery(getFetchUserProfileQueryOptions(username));

export const prefetchUserProfile = (
  queryClient: QueryClient,
  username: string,
) => queryClient.prefetchQuery(getFetchUserProfileQueryOptions(username));
