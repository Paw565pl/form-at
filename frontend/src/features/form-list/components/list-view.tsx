import { FormCard } from "@/core/components/form-card/form-card";
import { forms } from "@/features/form-list/example-forms";

export const ListView = () => {
  return (
    <div className="flex flex-col gap-2">
      {forms.map((form) => (
        <FormCard key={form.id} form={form} showAuthor />
      ))}
    </div>
  );
};
