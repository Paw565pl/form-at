export enum AchievementType {
  FORMS_CREATED = "FORMS_CREATED",
  SUBMISSIONS_RECEIVED = "SUBMISSIONS_RECEIVED",
  PERFECT_SCORES = "PERFECT_SCORES",
  FORMS_COMPLETED = "FORMS_COMPLETED",
}

export const ACHIEVEMENT_TRANSLATION_KEYS = {
  [AchievementType.FORMS_CREATED]: "formsCreated",
  [AchievementType.SUBMISSIONS_RECEIVED]: "submissionsReceived",
  [AchievementType.PERFECT_SCORES]: "perfectScores",
  [AchievementType.FORMS_COMPLETED]: "formsCompleted",
} as const;

export enum AchievementLevel {
  LEVEL_1 = "LEVEL_1",
  LEVEL_2 = "LEVEL_2",
  LEVEL_3 = "LEVEL_3",
  LEVEL_4 = "LEVEL_4",
}

export interface UserAchievement {
  readonly id: string;
  readonly type: AchievementType;
  readonly level: AchievementLevel;
  readonly threshold: number;
}
