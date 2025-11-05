import { auth } from "@/features/auth/config/auth-config";
import {
  defaultRedirectPath,
  protectedRoutes,
} from "@/features/auth/config/protected-routes";
import { NextResponse } from "next/server";

export const middleware = auth(({ nextUrl, auth }) => {
  const currentPathName = nextUrl.pathname;
  const protectedRoute = protectedRoutes.find((page) =>
    page.pattern.test(currentPathName),
  );

  if (!protectedRoute) return NextResponse.next();

  const isUserLoggedIn = !!auth;
  if (!isUserLoggedIn) {
    const redirectUrl = nextUrl.clone();

    redirectUrl.pathname = protectedRoute.redirectTo;
    if (protectedRoute.redirectTo === defaultRedirectPath)
      redirectUrl.searchParams.set("redirectTo", currentPathName);

    return NextResponse.redirect(redirectUrl);
  }

  if (protectedRoute.roles.length > 0) {
    const userRoles = auth.user.roles;
    const hasRequiredRoles = protectedRoute.roles.every((role) =>
      userRoles.includes(role),
    );

    if (!hasRequiredRoles) {
      const redirectUrl = nextUrl.clone();

      redirectUrl.pathname = protectedRoute.redirectTo;
      if (protectedRoute.redirectTo === defaultRedirectPath)
        redirectUrl.searchParams.set("redirectTo", currentPathName);

      return NextResponse.redirect(redirectUrl);
    }
  }

  return NextResponse.next();
});

export const config = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico).*)"],
};
