import { ProtectedRoutePattern } from "@/features/auth/types/protected-route-pattern";
import { Role } from "@/features/auth/types/role";

export const defaultRedirectPath = "/api/sign-in";

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

export const protectedRoutes: ProtectedRoutePattern[] = [
  createProtectedRoutePattern.exact("/forms/new"),
  createProtectedRoutePattern.exact("/forms/[^/]+/edit"),
  createProtectedRoutePattern.exact("/forms/[^/]+/statistics"),
  createProtectedRoutePattern.exact("/forms/[^/]+/submissions"),
  createProtectedRoutePattern.exact("/forms/[^/]+/submissions/[^/]+"),
] as const;
