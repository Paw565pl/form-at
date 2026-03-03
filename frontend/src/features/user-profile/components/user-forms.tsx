import { FormListItem } from "@/core/components/form-card/form-card";
import { Button } from "@/core/components/ui/button";
import {
  Empty,
  EmptyContent,
  EmptyHeader,
  EmptyTitle,
} from "@/core/components/ui/empty";
import { ICONS } from "@/core/config/icons";
import { FormListItemSkeleton } from "@/features/form-list/form-list-loading";
import { useFetchFormPages } from "@/features/form-list/hooks/use-fetch-form-pages";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import Link from "next/link";

interface UserFormsProps {
  readonly authorId: string;
}

export const UserForms = ({ authorId }: UserFormsProps) => {
  const t = useTranslations("userProfilePage.usersForms");
  const { data: session } = useSession();
  const { data: formPages, isLoading } = useFetchFormPages(
    { authorId },
    undefined,
    {
      size: 3,
    },
  );

  if (isLoading)
    return (
      <div className="flex flex-2 flex-col gap-2">
        <h2 className="text-lg font-bold md:text-xl">{t("forms")}</h2>
        {Array.from({ length: 3 }).map((_, i) => (
          <FormListItemSkeleton key={i} />
        ))}
      </div>
    );

  const forms = formPages?.pages.at(0)?.content;

  if (!forms || forms.length === 0)
    return (
      <Empty>
        <EmptyHeader>
          <EmptyTitle>{t("noForms")}</EmptyTitle>
        </EmptyHeader>

        {session?.user.id === authorId && (
          <EmptyContent>
            <Link href="/forms/new">
              <Button>
                <ICONS.formNew />
                {t("createForm")}
              </Button>
            </Link>
          </EmptyContent>
        )}
      </Empty>
    );

  return (
    <div className="flex flex-2 flex-col gap-2">
      <h2 className="text-lg font-bold md:text-xl">{t("forms")}</h2>

      {forms?.map((form) => (
        <FormListItem form={form} key={form.id} />
      ))}

      <Link
        href={`/forms?authorId=${authorId}`}
        className="text-muted-foreground mt-auto text-right text-sm hover:underline"
      >
        {t("showAll")}
      </Link>
    </div>
  );
};
