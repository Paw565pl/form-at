import { getQueryClient } from "@/core/lib/tanstack-query";
import { prefetchFormPages } from "@/features/form-list/hooks/use-fetch-form-pages";
import { UserProfile } from "@/features/user-profile/components/user-profile";
import {
  getFetchUserProfileQueryOptions,
  prefetchUserProfile,
} from "@/features/user-profile/hooks/use-fetch-user-profile";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const UserProfilePage = async ({
  params,
}: PageProps<"/users/[username]">) => {
  const { username } = await params;

  const queryClient = getQueryClient();
  await prefetchUserProfile(queryClient, username);

  const userProfile = queryClient.getQueryData(
    getFetchUserProfileQueryOptions(username).queryKey,
  );
  if (userProfile?.id) {
    await prefetchFormPages(
      queryClient,
      { authorId: userProfile.id },
      undefined,
      {
        size: 3,
      },
    );
  }

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <UserProfile username={username} />
    </HydrationBoundary>
  );
};
