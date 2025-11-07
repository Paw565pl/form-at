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
import { Brain, HatGlasses, ListChecks, MoreHorizontal } from "lucide-react";
import { useTranslations } from "next-intl";
import Image from "next/image";

export const Banner = () => {
  const t = useTranslations("PublicFormView.Banner");
  return (
    <div className="relative mb-7 flex h-48 w-full max-w-5xl items-end">
      <Image
        src="/banner.jpg"
        alt="Background"
        fill
        style={{ objectFit: "cover" }}
        priority
        className="-z-10 rounded-3xl"
      />
      <Button size={"icon-sm"} className="absolute top-4 right-4">
        <MoreHorizontal />
      </Button>
      <div
        className={cn(
          "absolute flex items-center justify-center rounded-full border-2 border-black bg-white md:-bottom-6 md:left-4 md:h-24 md:w-24",
          "-bottom-4 left-2 h-16 w-16",
        )}
      >
        <Brain className="h-12 w-12 md:h-16 md:w-16" />
      </div>
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
                {t("takeTheQuiz")}
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
        <Button size={"sm"}>
          <HatGlasses />
          {t("fillAnonymously")}
        </Button>
      </div>
    </div>
  );
};
