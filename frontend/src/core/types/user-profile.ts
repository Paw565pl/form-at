import { BadgeVariant } from "@/core/components/ui/badge";
import { UserAchievement } from "@/core/types/achievement";
import { User } from "@/features/auth/types/user";

export interface UserProfileDto extends User {
  readonly description?: string;
  readonly achievements: UserAchievement[];
  readonly history: UserHistoryItemDto[];
}

export interface UserHistoryItemDto {
  readonly id: string;
  readonly content: string;
  readonly formName: string;
  readonly date: Date;
  readonly badgeVariant?: BadgeVariant;
}
