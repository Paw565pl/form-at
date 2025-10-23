export enum Role {
  ADMIN = "admin",
}

export const isRole = (value: string): value is Role =>
  (Object.values(Role) as string[]).includes(value.toLowerCase());
