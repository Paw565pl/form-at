"use client";

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
import { forms } from "@/features/form-list/example-forms";
import { useTranslations } from "next-intl";
import { useState } from "react";

export const Forms = () => {
  const t = useTranslations("formListPage");
  const [isGridLayout, setIsGridLayout] = useState(true);

  return (
    <>
      <header className="mb-2 flex flex-wrap items-center justify-between gap-4">
        <h1 className="ml-4 text-xl font-bold">
          {t("title", { count: forms.length })}
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
    </>
  );
};
