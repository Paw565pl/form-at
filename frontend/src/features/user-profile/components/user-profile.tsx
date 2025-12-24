"use client";

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

  return <section></section>;
};
