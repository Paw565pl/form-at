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

import { Brain, HatGlasses, ListChecks, MoreHorizontal } from "lucide-react";
import { useTranslations } from "next-intl";
import Image from "next/image";

export const Banner = () => {
  const t = useTranslations("PublicFormView.Banner");
  return (
    <div className="relative mb-[25px] flex h-[200px] w-full max-w-5xl items-end">
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
      <div className="bg-muted-foreground absolute bottom-[-25px] left-4 flex h-[100px] w-[100px] items-center justify-center rounded-full border-2 border-black">
        <Brain className="h-16 w-16" />
      </div>
      <div className="absolute right-4 bottom-4 flex gap-2">
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
                <Input id="code" name="code" placeholder="code" />
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
