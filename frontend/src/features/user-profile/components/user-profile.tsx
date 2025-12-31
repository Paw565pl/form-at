"use client";

import { Card, CardDescription, CardTitle } from "@/core/components/ui/card";
import { UserImage } from "@/core/components/user-image/user-image";
import { useFetchFormPages } from "@/features/form-list/hooks/use-fetch-form-pages";
import { UserForms } from "@/features/user-profile/components/user-forms";
import { useFetchUserProfile } from "@/features/user-profile/hooks/use-fetch-user-profile";
import { HttpStatusCode } from "axios";
import { useTranslations } from "next-intl";
import { notFound } from "next/navigation";

interface UserProfileProps {
  readonly username: string;
}

export const UserProfile = ({ username }: UserProfileProps) => {
  const t = useTranslations("userProfilePage");
  const {
    data: userProfile,
    isLoading: isProfileLoading,
    error,
  } = useFetchUserProfile(username);
  const { data: formPages, isLoading: isFormsLoading } = useFetchFormPages(
    { authorId: userProfile?.id ?? null },
    { sort: "createdAt,desc" },
  );

  if (error) {
    if (error.status === HttpStatusCode.NotFound) return notFound();
    else throw error;
  }

  if (!userProfile || isProfileLoading) return <p>{t("loadingProfile")}</p>;

  return (
    <section
      id="user-profile"
      className="flex w-full flex-col gap-4 px-5 py-10 lg:px-30"
    >
      <div className="flex flex-col gap-4 md:flex-row">
        {/* Profile Header */}
        <Card className="flex flex-1 flex-col items-center justify-center gap-2 p-3">
          <UserImage className="h-48 w-48" />
          <h1 className="text-2xl font-bold md:text-3xl">
            {userProfile.username}
          </h1>
        </Card>

        {/* User's Forms */}
        <UserForms formPages={formPages} isFormsLoading={isFormsLoading} />
      </div>

      {/* Statistics */}
      <h2 className="text-lg font-bold md:text-xl">{t("userStatistics")}</h2>
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
    </section>
  );
};
