import { getQueryClient } from "@/core/lib/tanstack-query";
import { UserProfile } from "@/features/user-profile/components/user-profile";
import { prefetchUserProfile } from "@/features/user-profile/hooks/use-fetch-user-profile";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const UserProfilePage = async ({
  params,
}: PageProps<"/users/[username]">) => {
  const { username } = await params;

  const queryClient = getQueryClient();
  await prefetchUserProfile(queryClient, username);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <UserProfile username={username} />
    </HydrationBoundary>
  );
};
