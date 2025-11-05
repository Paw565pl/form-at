import { Tokens } from "@/features/auth/types/tokens";
import { User } from "@/features/auth/types/user";

declare module "next-auth" {
  /**
   * Decoded id token properies.
   */
  interface Profile {
    realm_access: {
      roles: string[];
    };
  }

  /**
   * Returned by `useSession`, `auth`, contains information about the active session.
   */
  interface Session {
    readonly user: User;
    readonly tokens: Tokens;
  }
}

import "next-auth/jwt";

declare module "next-auth/jwt" {
  /** Returned by the `jwt` callback and `auth`, when using JWT sessions */
  interface JWT {
    readonly user: User;
    readonly tokens: Tokens;
  }
}
