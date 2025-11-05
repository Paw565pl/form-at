import { ProtectedRoutePattern } from "@/features/auth/types/protected-route-pattern";
import { Role } from "@/features/auth/types/role";

export const defaultRedirectPath = "/api/sign-in";

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const createProtectedRoutePattern = {
  exact: (
    path: string,
    redirectTo: string = defaultRedirectPath,
    roles: Role[] = [],
  ): ProtectedRoutePattern => ({
    pattern: new RegExp(`^${path}$`),
    redirectTo,
    roles,
  }),

  withChildren: (
    path: string,
    redirectTo: string = defaultRedirectPath,
    roles: Role[] = [],
  ): ProtectedRoutePattern => ({
    pattern: new RegExp(`${path}(/.*)?$`),
    redirectTo,
    roles,
  }),
} as const;

export const protectedRoutes: ProtectedRoutePattern[] = [] as const;
