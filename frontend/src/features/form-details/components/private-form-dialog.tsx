import { Button } from "@/core/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/core/components/ui/dialog";
import { Input } from "@/core/components/ui/input";
import { useTranslations } from "next-intl";

interface PrivateFormDialogProps {
  readonly formIdOrSlug: string;
}

export const PrivateFormDialog = ({ formIdOrSlug }: PrivateFormDialogProps) => {
  const t = useTranslations("formDetailsPage.banner");

  return (
    <Dialog open={true}>
      <form>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{t("enterTheCode")}</DialogTitle>
            <DialogDescription>{t("codeDescription")}</DialogDescription>
          </DialogHeader>
          <div className="grid gap-3">
            <Input id="code" name="code" placeholder={t("code")} />
          </div>
          <DialogFooter>
            <Button type="submit">{t("confirm")}</Button>
          </DialogFooter>
        </DialogContent>
      </form>
    </Dialog>
  );
};
