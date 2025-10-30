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

import { HatGlasses, ListChecks, MoreHorizontal } from "lucide-react";
import Image from "next/image";

export const Banner = () => {
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
      <div className="absolute bottom-[-25px] left-4 h-[100px] w-[100px] rounded-full border-2 border-black bg-red-500"></div>
      <div className="absolute right-4 bottom-4 flex gap-2">
        <Dialog>
          <form>
            <DialogTrigger asChild>
              <Button size={"sm"}>
                <ListChecks />
                Wykonaj Quiz
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Podaj hasło do quizu</DialogTitle>
                <DialogDescription>
                  Ten quiz jest zabezpieczony hasłem, zapytaj twórcę quizu o
                  hasło
                </DialogDescription>
              </DialogHeader>
              <div className="grid gap-3">
                <Label htmlFor="code">Code</Label>
                <Input id="code" name="code" placeholder="code" />
              </div>
              <DialogFooter>
                <DialogClose asChild>
                  <Button variant="outline">Cancel</Button>
                </DialogClose>
                <Button type="submit">Save changes</Button>
              </DialogFooter>
            </DialogContent>
          </form>
        </Dialog>
        <Button size={"sm"}>
          <HatGlasses />
          Wykonaj Anonimowo
        </Button>
      </div>
    </div>
  );
};
