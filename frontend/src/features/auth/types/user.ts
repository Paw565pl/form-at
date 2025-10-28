import { Role } from "@/features/auth/types/role";

export interface User {
  readonly id: string;
  readonly name: string;
  readonly email: string;
  readonly image?: string | null;
  readonly roles: Role[];
}
