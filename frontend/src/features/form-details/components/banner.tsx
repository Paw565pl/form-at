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
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { FormImageWithFallback } from "@/core/form-image/form-image-with-fallback";
import { FormDetailResponseDto } from "@/core/types/form";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";

interface BannerProps {
  readonly form: FormDetailResponseDto;
}

export const Banner = ({ form }: BannerProps) => {
  const t = useTranslations("formDetailsPage.banner");
  const router = useRouter();

  return (
    <section className="relative flex h-48 w-full items-end">
      {/* background image */}
      <FormImageWithFallback
        src={form.thumbnail}
        alt="Background"
        fill
        className="rounded-t-md object-cover"
      />

      {/* go back button */}
      <span className="absolute top-4 left-4">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button
              aria-label={t("back")}
              size="icon-sm"
              onClick={() => router.back()}
            >
              <ICONS.back />
            </Button>
          </TooltipTrigger>
          <TooltipContent side="bottom">
            <span>{t("back")}</span>
          </TooltipContent>
        </Tooltip>
      </span>

      {/* more options */}
      <span className="absolute top-4 right-4">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button aria-label={t("moreOptions")} size="icon-sm">
              <ICONS.more />
            </Button>
          </TooltipTrigger>
          <TooltipContent side="bottom">
            <span>{t("moreOptions")}</span>
          </TooltipContent>
        </Tooltip>
      </span>

      <div className="absolute right-2 bottom-2 flex flex-col items-end gap-2 md:right-4 md:bottom-4 md:flex-row">
        <Dialog>
          <form>
            <DialogTrigger asChild>
              <Button>
                <ICONS.fillForm />
                {t("fillOutForm")}
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>{t("enterTheCode")}</DialogTitle>
                <DialogDescription>{t("codeDescription")}</DialogDescription>
              </DialogHeader>
              <div className="grid gap-3">
                <Input id="code" name="code" placeholder={t("code")} />
              </div>
              <DialogFooter>
                <DialogClose asChild>
                  <Button variant="outline">{t("cancel")}</Button>
                </DialogClose>
                <Button type="submit">{t("confirm")}</Button>
              </DialogFooter>
            </DialogContent>
          </form>
        </Dialog>

        {form.allowsGuestSubmissions && (
          <Button>
            <ICONS.anonymous />
            {t("fillAnonymously")}
          </Button>
        )}
      </div>
    </section>
  );
};
