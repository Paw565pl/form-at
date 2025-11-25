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
import {
  formFilterSearchParams,
  formSortSearchParams,
} from "@/features/form-list/search-params/form-search-params";
import { useTranslations } from "next-intl";
import { useQueryStates } from "nuqs";
import { useState } from "react";

export const Forms = () => {
  const t = useTranslations("formListPage");
  const [isGridLayout, setIsGridLayout] = useState(true);
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
                  size="icon"
                  variant={isGridLayout ? "default" : "outline"}
                  onClick={() => setIsGridLayout(true)}
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
                  size="icon"
                  variant={isGridLayout ? "outline" : "default"}
                  onClick={() => setIsGridLayout(false)}
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

      {isGridLayout ? <GridView /> : <ListView />}

      <ScrollToTopButton />
    </section>
  );
};
