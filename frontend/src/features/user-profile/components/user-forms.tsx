import { FormCard } from "@/core/components/form-card/form-card";
import { Button } from "@/core/components/ui/button";
import { Empty, EmptyContent, EmptyHeader } from "@/core/components/ui/empty";
import { ICONS } from "@/core/config/icons";
import { FormListResponseDto } from "@/core/types/form";
import { useTranslations } from "next-intl";
import Link from "next/link";

interface UserFormsProps {
  readonly forms: FormListResponseDto[] | undefined;
  readonly isLoading: boolean;
}

export const UserForms = ({ forms, isLoading }: UserFormsProps) => {
  const t = useTranslations("userProfilePage.usersForms");

  if (isLoading)
    return <p className="text-lg font-bold">{t("loadingForms")}</p>;

  if (!forms || forms.length === 0)
    return (
      <Empty>
        <EmptyHeader>{t("noForms")}</EmptyHeader>
        <EmptyContent>
          <Button asChild>
            <Link href="/forms/new">
              <ICONS.formNew />
              {t("createForm")}
            </Link>
          </Button>
        </EmptyContent>
      </Empty>
    );

  return (
    <div className="flex flex-2 flex-col gap-2">
      <h2 className="text-lg font-bold md:text-xl">{t("forms")}</h2>
      {forms?.map((form) => (
        <FormCard form={form} key={form.id} />
      ))}
    </div>
  );
};
