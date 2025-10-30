"use client";

import { Button } from "@/core/components/ui/button";
import { Input } from "@/core/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/core/components/ui/select";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { forms } from "@/features/public-forms-list-view/forms";
import { LayoutGrid, List } from "lucide-react";
import { useTranslations } from "next-intl";
import { useRef, useState } from "react";
import { GridView } from "@/features/public-forms-list-view/components/grid-view";
import { ListView } from "@/features/public-forms-list-view/components/list-view";

export const PublicFormsListView = () => {
  // eslint-disable-next-line
  const [loading, setLoading] = useState(false);
  const [gridLayout, setGridLayout] = useState(true);
  const loader = useRef<HTMLDivElement | null>(null);
  const t = useTranslations("publicFormsList");

  return (
    <main
      id="PublicFormsListView"
      className="forms-list flex w-full flex-col gap-2 px-5 py-10 lg:px-30"
    >
      <header className="mb-2 flex flex-wrap items-center justify-between gap-4">
        <h1 className="ml-4 text-xl font-bold">
          {t("title", { count: forms.length.toString() })}
        </h1>
        <div id="FormFilterBar" className="flex flex-wrap gap-2 md:flex-nowrap">
          <form
            className="w-full max-w-70"
            onSubmit={(e) => {
              e.preventDefault();
              // To-do: Implement search functionality
            }}
          >
            <Input
              placeholder={t("options.searchPlaceholder")}
              type="text"
              className="md:min-w-60"
            />
          </form>

          <Select defaultValue={"newest"}>
            <SelectTrigger className="w-full max-w-70">
              {t("options.sortBy")}
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="newest">
                {t("options.sortOptions.newest")}
              </SelectItem>
              <SelectItem value="oldest">
                {t("options.sortOptions.oldest")}
              </SelectItem>
              <SelectItem value="name">
                {t("options.sortOptions.name")}
              </SelectItem>
              <SelectItem value="questions">
                {t("options.sortOptions.questions")}
              </SelectItem>
              <SelectItem value="submissions">
                {t("options.sortOptions.submissions")}
              </SelectItem>
              <SelectItem value="duration">
                {t("options.sortOptions.duration")}
              </SelectItem>
            </SelectContent>
          </Select>

          <div className="flex gap-2">
            <Tooltip>
              <TooltipTrigger asChild>
                <Button
                  size="icon"
                  variant={gridLayout ? "default" : "outline"}
                  onClick={() => setGridLayout(true)}
                >
                  <LayoutGrid />
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
                  <List />
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
