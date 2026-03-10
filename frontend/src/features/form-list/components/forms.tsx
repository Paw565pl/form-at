"use client";

import { ScrollToTopButton } from "@/core/components/scroll-to-top-button/scroll-to-top-button";
import { Button } from "@/core/components/ui/button";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { Filters } from "@/features/form-list/components/filters";
import { GridView } from "@/features/form-list/components/grid-view";
import { ListView } from "@/features/form-list/components/list-view";
import { useFetchFormPages } from "@/features/form-list/hooks/use-fetch-form-pages";
import { useFormListLayout } from "@/features/form-list/hooks/use-form-list-layout";
import {
  formFilterSearchParams,
  formSortSearchParams,
} from "@/features/form-list/search-params/form-search-params";
import { useTranslations } from "next-intl";
import { useQueryStates } from "nuqs";
import { Activity } from "react";

export type FormListLayout = "grid" | "list";

interface FormsProps {
  readonly initialFormListLayout: FormListLayout;
}

export const Forms = ({ initialFormListLayout }: FormsProps) => {
  const t = useTranslations("formListPage");
  const [formListLayout, setFormListLayout] = useFormListLayout(
    initialFormListLayout,
  );

  const [filtersDto] = useQueryStates(formFilterSearchParams);
  const [sortDto] = useQueryStates(formSortSearchParams);
  const { data: formPages, error } = useFetchFormPages(filtersDto, sortDto);

  if (error) throw error;

  const totalElements = formPages?.pages.at(0)?.page.totalElements || 0;

  return (
    <section
      id="forms-list"
      className="flex w-full flex-col gap-2 px-5 py-10 lg:px-30"
    >
      <header className="mb-2 flex flex-wrap items-center justify-between gap-4">
        <h1 className="ml-4 text-xl font-bold">
          {t("title", { count: totalElements })}
        </h1>
        <div className="flex flex-wrap justify-between gap-2 md:flex-nowrap">
          <Filters />

          <div className="flex gap-2">
            <Tooltip>
              <TooltipTrigger asChild>
                <Button
                  aria-label={t("options.gridView")}
                  size="icon"
                  variant={formListLayout === "grid" ? "default" : "outline"}
                  onClick={() => setFormListLayout("grid")}
                >
                  <ICONS.grid />
                </Button>
              </TooltipTrigger>
              <TooltipContent side="bottom">
                <p>{t("options.gridView")}</p>
              </TooltipContent>
            </Tooltip>

            <Tooltip>
              <TooltipTrigger asChild>
                <Button
                  aria-label={t("options.listView")}
                  size="icon"
                  variant={formListLayout === "list" ? "default" : "outline"}
                  onClick={() => setFormListLayout("list")}
                >
                  <ICONS.list />
                </Button>
              </TooltipTrigger>
              <TooltipContent side="bottom">
                <p>{t("options.listView")}</p>
              </TooltipContent>
            </Tooltip>
          </div>
        </div>
      </header>

      <Activity mode={formListLayout === "grid" ? "visible" : "hidden"}>
        <GridView />
      </Activity>

      <Activity mode={formListLayout === "list" ? "visible" : "hidden"}>
        <ListView />
      </Activity>

      <ScrollToTopButton />
    </section>
  );
};
