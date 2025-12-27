"use client";

import { UserImage } from "@/core/components/user-image/user-image";
import { StatisticsCard } from "@/features/user-profile/components/statistics-card";
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
    <section
      id="user-profile"
      className="flex w-full flex-col gap-2 px-5 py-10 lg:px-30"
    >
      <div className="bg-card rounded-lg border p-6">
        {/* Profile Header */}
        <div className="flex items-start gap-4 pb-6">
          <UserImage className="h-48 w-48" />
          <h1 className="pt-8 text-2xl font-bold md:text-3xl">
            {userProfile.username}
          </h1>
        </div>

        {/* Statistics */}
        <div className="flex flex-wrap gap-4">
          <StatisticsCard
            value={userProfile.statistics.formsCount}
            label={t("formsCreated")}
          />
          <StatisticsCard
            value={userProfile.statistics.submissionsCount}
            label={t("submissions")}
          />
          <StatisticsCard
            value={userProfile.statistics.commentsCount}
            label={t("comments")}
          />
        </div>
      </div>
    </section>
  );
};
