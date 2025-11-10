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
import { useRef, useState } from "react";

export const FormListPage = () => {
  // eslint-disable-next-line
  const [loading, setLoading] = useState(false);
  const [gridLayout, setGridLayout] = useState(true);
  const loader = useRef<HTMLDivElement | null>(null);
  const t = useTranslations("formListPage");

  return (
    <main className="forms-list flex w-full flex-col gap-2 px-5 py-10 lg:px-30">
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
                  variant={gridLayout ? "default" : "outline"}
                  onClick={() => setGridLayout(true)}
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
                  variant={gridLayout ? "outline" : "default"}
                  onClick={() => setGridLayout(false)}
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

      {gridLayout ? <GridView /> : <ListView />}

      <div ref={loader} className="h-px-40" />

      {loading && <p>{t("loading")}</p>}

      <p className="text-muted-foreground p-4 text-center text-sm">
        {t("end")}
      </p>
    </main>
  );
};
