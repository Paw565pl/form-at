import { Badge } from "@/core/components/ui/badge";
import {
  ACHIEVEMENT_TRANSLATION_KEYS,
  UserAchievement,
} from "@/core/types/achievement";
import { PartyPopper } from "lucide-react";
import { useTranslations } from "next-intl";

interface AchievementsProps {
  achievements: UserAchievement[];
}

export const Achievements = ({ achievements }: AchievementsProps) => {
  const t = useTranslations("userProfilePage.achievements");

  if (achievements.length === 0) {
    return (
      <div className="flex gap-1 p-1">
        <p className="self-center">{t("noAchievements")}</p>
      </div>
    );
  }

  return (
    <div className="flex gap-1 p-1">
      {achievements.map((achievement) => {
        const translationKey = ACHIEVEMENT_TRANSLATION_KEYS[achievement.type];
        return (
          <Badge key={achievement.id} className="text-white">
            <PartyPopper />
            {t(translationKey, { count: achievement.threshold })}
          </Badge>
        );
      })}
    </div>
  );
};
