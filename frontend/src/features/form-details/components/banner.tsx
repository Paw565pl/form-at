"use client";

import { Button } from "@/core/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/core/components/ui/dialog";
import { Input } from "@/core/components/ui/input";
import { Label } from "@/core/components/ui/label";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { FormResponseDto } from "@/core/types/form";
import { placeholder_image_url } from "@/features/form-list/example-forms";
import { useTranslations } from "next-intl";
import Image from "next/image";
import { useRouter } from "next/navigation";

interface BannerProps {
  readonly form: FormResponseDto;
}

export const Banner = ({ form }: BannerProps) => {
  const t = useTranslations("publicFormView.banner");
  const router = useRouter();

  return (
    <section className="relative flex h-48 w-full items-end">
      {/* background image */}
      <Image
        src={form.thumbnailKey || placeholder_image_url}
        alt="Background"
        fill
        style={{ objectFit: "cover" }}
        priority
        className="-z-10 rounded-3xl"
      />

      {/* go back button */}
      <span className="absolute top-4 left-4">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button size="icon" variant="default" onClick={() => router.back()}>
              <ICONS.back />
            </Button>
          </TooltipTrigger>
          <TooltipContent side="bottom">
            <p>{t("back")}</p>
          </TooltipContent>
        </Tooltip>
      </span>

      {/* more options */}
      <span className="absolute top-4 right-4">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button size="icon" variant="default">
              <ICONS.more />
            </Button>
          </TooltipTrigger>
          <TooltipContent side="bottom">
            <p>{t("moreOptions")}</p>
          </TooltipContent>
        </Tooltip>
      </span>

      <div className="absolute right-2 bottom-2 flex flex-col items-end gap-2 md:right-4 md:bottom-4 md:flex-row">
        <Dialog>
          <form>
            <DialogTrigger asChild>
              <Button size={"sm"}>
                <ICONS.listChecks />
                {t("fillOutForm")}
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>{t("enterTheCode")}</DialogTitle>
                <DialogDescription>{t("codeDescription")}</DialogDescription>
              </DialogHeader>
              <div className="grid gap-3">
                <Label htmlFor="code">{t("code")}</Label>
                <Input id="code" name="code" placeholder={t("code")} />
              </div>
              <DialogFooter>
                <DialogClose asChild>
                  <Button variant="outline">{t("cancel")}</Button>
                </DialogClose>
                <Button type="submit">{t("saveChanges")}</Button>
              </DialogFooter>
            </DialogContent>
          </form>
        </Dialog>

        {form.allowsGuestSubmissions ? (
          <Button size={"sm"}>
            <ICONS.hatGlasses />
            {t("fillAnonymously")}
          </Button>
        ) : null}
      </div>
    </section>
  );
};
