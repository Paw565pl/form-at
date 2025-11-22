import { AuthButton } from "@/core/components/auth-button/auth-button";
import { Logo } from "@/core/components/logo/logo";
import { LanguageSwitcher } from "@/core/components/nav-bar/language-switcher";
import { MobileMenu } from "@/core/components/nav-bar/mobile-menu";
import { ThemeSwitcher } from "@/core/components/nav-bar/theme-switcher";
import { Button } from "@/core/components/ui/button";
import { ICONS } from "@/core/config/icons";
import { auth } from "@/features/auth/config/auth-config";
import { getTranslations } from "next-intl/server";
import Link from "next/link";

export const NavBar = async () => {
  const t = await getTranslations("navBar");
  const session = await auth();

  return (
    <nav className="flex w-full justify-between p-2">
      <div className="links flex items-center gap-4">
        <div className="text-primary flex items-center text-lg font-semibold">
          <Logo />
          <h2>formAT</h2>
        </div>

        <div className="hidden gap-2 md:flex">
          <Button size="sm" asChild variant="outline">
            <Link href="/">
              <ICONS.home />
              {t("home")}
            </Link>
          </Button>
          <Button size="sm" asChild variant="outline">
            <Link href="/forms">
              <ICONS.form />
              {t("forms")}
            </Link>
          </Button>
          {session && (
            <Button size="sm" asChild>
              <Link href="/forms/new">
                <ICONS.formNew />
                {t("createForm")}
              </Link>
            </Button>
          )}
        </div>
      </div>

      <div className="actions flex items-center gap-2">
        {session && (
          <Button size="sm" asChild className="hidden md:flex">
            <Link href="/profile">
              <ICONS.user />
              {session?.user?.name}
            </Link>
          </Button>
        )}

        <div className="hidden md:inline">
          <AuthButton />
        </div>
        <ThemeSwitcher />
        <LanguageSwitcher />
        <MobileMenu />
      </div>
    </nav>
  );
};
