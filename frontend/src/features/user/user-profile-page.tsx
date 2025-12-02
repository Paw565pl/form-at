"use client";

import { FormCard } from "@/core/components/form-card/form-card";
import { AchievementLevel, AchievementType } from "@/core/types/achievement";
import { UserProfile } from "@/core/types/user-profile";
import type { User } from "@/features/auth/types/user";
import { forms } from "@/features/form-list/example-forms";
import { Achievements } from "@/features/user/components/achievements";
import { History } from "@/features/user/components/history";
import { useTranslations } from "next-intl";
import Image from "next/image";

const User: UserProfile = {
  id: "1",
  name: "John Doe",
  email: "john.doe@example.com",
  image:
    "https://www.svgrepo.com/show/382109/male-avatar-boy-face-man-user-7.svg",
  description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
  roles: [],
  achievements: [
    {
      id: "1",
      type: AchievementType.FORMS_CREATED,
      level: AchievementLevel.LEVEL_2,
      threshold: 5,
    },
    {
      id: "2",
      type: AchievementType.SUBMISSIONS_RECEIVED,
      level: AchievementLevel.LEVEL_3,
      threshold: 100,
    },
  ],
  history: [
    {
      id: "1",
      content: "User John Doe has made submission to form: ",
      formName: "Quiz o kotach",
      date: new Date("2024-11-30"),
      badgeVariant: "secondary",
    },
    {
      id: "2",
      content: "User John Doe has created a new public form: ",
      formName: "Quiz o kotach",
      date: new Date("2024-11-30"),
      badgeVariant: "default",
    },
    {
      id: "3",
      content: "User John Doe has achived a perfect score in form: ",
      formName: "Quiz o kotach",
      date: new Date("2024-11-30"),
      badgeVariant: "secondary",
    },
  ],
};

const userForms = forms.slice(0, 3);

export const UserProfilePage = () => {
  const t = useTranslations("userProfilePage");

  return (
    <main className="px-5 py-10 md:px-0">
      <div className="flex flex-col gap-6 pb-6 md:flex-row">
        <section className="flex flex-1 flex-col gap-3">
          {/* user info  */}
          <header className="flex items-center gap-2">
            {User.image && (
              <Image
                className="rounded-full border-2 border-white"
                src={User.image}
                alt={User.name}
                width={100}
                height={100}
              />
            )}
            <span>
              <h1 className="pb-2 text-4xl font-semibold">{User.name}</h1>
              <h2 className="pb-2">{User.email}</h2>
            </span>
          </header>

          {/* achievements  */}
          <section className="bg-accent flex flex-col gap-2 rounded-md p-3 shadow-md">
            <h2 className="font-semibold">{t("achievementsLabel")}</h2>
            <Achievements achievements={User.achievements} />
          </section>

          {/* description  */}
          <section className="bg-accent rounded-md p-3 shadow-md">
            <p className="text-muted-foreground">{User.description}</p>
          </section>
        </section>

        <div className="flex flex-3 flex-col gap-4">
          {/* user forms  */}
          <section className="bg-accent flex w-full flex-1 flex-col gap-2 rounded-md p-3 shadow-sm">
            <h2 className="text-secondary-foreground font-semibold">
              {t("usersForms", { userName: User.name })}
            </h2>
            {userForms.map((form) => (
              <FormCard key={form.id} form={form} />
            ))}
          </section>

          {/* user history  */}
          <section className="bg-accent flex flex-col gap-2 rounded-md p-3 shadow-sm">
            <h3 className="text-secondary-foreground font-semibold">
              {t("usersHistory", { userName: User.name })}
            </h3>
            <History history={User.history} />
          </section>
        </div>
      </div>
    </main>
  );
};
