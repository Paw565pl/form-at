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
import { cn } from "@/core/lib/cn";
import { FormResponseDto } from "@/core/types/form";
import {
  CornerUpLeft,
  HatGlasses,
  ListChecks,
  MoreHorizontal,
} from "lucide-react";
import { useTranslations } from "next-intl";
import Image from "next/image";
import { useRouter } from "next/navigation";

interface BannerProps {
  form: FormResponseDto;
}

export const Banner = ({ form }: BannerProps) => {
  const t = useTranslations("publicFormView.banner");
  const router = useRouter();

  return (
    <main className="relative mb-7 flex h-48 w-full max-w-5xl items-end">
      {/* background image */}
      <Image
        src="/banner.jpg"
        alt="Background"
        fill
        style={{ objectFit: "cover" }}
        priority
        className="-z-10 rounded-3xl"
      />

      {/* go back button */}
      <Button
        size={"icon-sm"}
        className="absolute top-4 left-4"
        onClick={() => router.back()}
      >
        <CornerUpLeft />
      </Button>

      {/* more options */}
      <Button size={"icon-sm"} className="absolute top-4 right-4">
        <MoreHorizontal />
      </Button>

      <div
        className={cn(
          "absolute right-2 bottom-2 flex flex-col items-end gap-2 md:right-4 md:bottom-4",
          "md:flex-row",
        )}
      >
        <Dialog>
          <form>
            <DialogTrigger asChild>
              <Button size={"sm"}>
                <ListChecks />
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
            <HatGlasses />
            {t("fillAnonymously")}
          </Button>
        ) : null}
      </div>
    </main>
  );
};
