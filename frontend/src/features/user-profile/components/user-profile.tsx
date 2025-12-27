"use client";

import { UserImage } from "@/core/components/user-image/user-image";
import { useFetchUserProfile } from "@/features/user-profile/hooks/use-fetch-user-profile";
import { HttpStatusCode } from "axios";
import { useTranslations } from "next-intl";
import { notFound } from "next/navigation";

interface UserProfileProps {
  readonly username: string;
}

export const UserProfile = ({ username }: UserProfileProps) => {
  const t = useTranslations("userProfilePage");
  const { data: userProfile, isLoading, error } = useFetchUserProfile(username);

  if (error) {
    if (error.status === HttpStatusCode.NotFound) return notFound();
    else throw error;
  }

  if (!userProfile || isLoading) return <p>{t("loading")}</p>;

  return (
    <section className="mx-auto w-full max-w-4xl">
      <div className="bg-card rounded-lg border p-6">
        {/* Profile Header */}
        <div className="flex items-start gap-4 pb-6">
          <UserImage />
          <h1 className="pt-8 text-3xl font-bold">{userProfile.username}</h1>
        </div>

        {/* Statistics */}
        <div className="flex flex-wrap gap-4">
          <div className="flex flex-1 flex-col items-center rounded-md border p-4">
            <span className="text-2xl font-bold">
              {userProfile.statistics.formsCount}
            </span>
            <span className="text-muted-foreground text-sm">Forms Created</span>
          </div>

          <div className="flex flex-1 flex-col items-center rounded-md border p-4">
            <span className="text-2xl font-bold">
              {userProfile.statistics.submissionsCount}
            </span>
            <span className="text-muted-foreground text-sm">Submissions</span>
          </div>

          <div className="flex flex-1 flex-col items-center rounded-md border p-4">
            <span className="text-2xl font-bold">
              {userProfile.statistics.commentsCount}
            </span>
            <span className="text-muted-foreground text-sm">Comments</span>
          </div>
        </div>
      </div>
    </section>
  );
};
