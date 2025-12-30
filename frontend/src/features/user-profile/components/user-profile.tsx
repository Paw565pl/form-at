"use client";

import { Card, CardDescription, CardTitle } from "@/core/components/ui/card";
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
        <div className="flex flex-col gap-4 sm:flex-row">
          <Card className="flex flex-1 flex-col items-center rounded-md border p-4">
            <CardTitle>{userProfile.statistics.formsCount}</CardTitle>
            <CardDescription className="text-center">
              {t("formsCount")}
            </CardDescription>
          </Card>
          <Card className="flex flex-1 flex-col items-center rounded-md border p-4">
            <CardTitle>{userProfile.statistics.submissionsCount}</CardTitle>
            <CardDescription className="text-center">
              {t("submissionsCount")}
            </CardDescription>
          </Card>
          <Card className="flex flex-1 flex-col items-center rounded-md border p-4">
            <CardTitle>{userProfile.statistics.commentsCount}</CardTitle>
            <CardDescription className="text-center">
              {t("commentsCount")}
            </CardDescription>
          </Card>
        </div>
      </div>
    </section>
  );
};
