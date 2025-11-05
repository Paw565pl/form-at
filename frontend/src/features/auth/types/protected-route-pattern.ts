import { Role } from "@/features/auth/types/role";

export interface ProtectedRoutePattern {
  readonly pattern: RegExp;
  readonly redirectTo: string;
  readonly roles: Role[];
}
