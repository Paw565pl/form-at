import { FormCard } from "@/core/components/form-card/form-card";
import { Button } from "@/core/components/ui/button";
import { Empty, EmptyContent, EmptyHeader } from "@/core/components/ui/empty";
import { ICONS } from "@/core/config/icons";
import { FormListResponseDto } from "@/core/types/form";
import type { PaginatedResponseDto } from "@/core/types/paginated-response-dto";
import { InfiniteData } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import Link from "next/link";

interface UserFormsProps {
  readonly formPages:
    | InfiniteData<PaginatedResponseDto<FormListResponseDto>, unknown>
    | undefined;
  readonly isFormsLoading: boolean;
}

export const UserForms = ({ formPages, isFormsLoading }: UserFormsProps) => {
  const t = useTranslations("userProfilePage.usersForms");

  if (isFormsLoading) {
    return <p className="text-lg font-bold">{t("loadingForms")}</p>;
  }

  if (!formPages?.pages.at(0)?.content.length) {
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
  }

  return (
    <div className="flex flex-1 flex-col gap-2">
      <h2 className="text-lg font-bold md:text-xl">{t("forms")}</h2>
      {formPages?.pages.map((page) =>
        page.content
          .slice(0, 4)
          .map((form) => <FormCard form={form} key={form.id} />),
      )}
    </div>
  );
};
