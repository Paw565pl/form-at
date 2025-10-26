import { Button } from "@/core/components/ui/button";
import { auth, signIn, signOut } from "@/features/auth/config/auth-config";

export const AuthButton = async () => {
  const session = await auth();

  if (!session?.user)
    return (
      <form
        action={async () => {
          "use server";
          await signIn("keycloak");
        }}
      >
        <Button type="submit">Sign In</Button>
      </form>
    );

  return (
    <form
      action={async () => {
        "use server";
        await signOut();
      }}
    >
      <Button type="submit">Sign Out</Button>
    </form>
  );
};
