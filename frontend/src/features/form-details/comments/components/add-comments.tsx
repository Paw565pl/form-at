import { Button } from "@/core/components/ui/button";
import { Input } from "@/core/components/ui/input";
import { ICONS } from "@/core/config/icons";
import { useSession } from "next-auth/react";
import { FormEvent, useState } from "react";
import { useCreateComment } from "../hooks/use-create-comment";

interface AddCommentsProps {
  formIdOrSlug: string;
}

export const AddComments = ({ formIdOrSlug }: AddCommentsProps) => {
  const { data: session } = useSession();
  const [content, setContent] = useState("");
  const { mutate: createComment, isPending } = useCreateComment({
    formIdOrSlug,
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();

    if (!content.trim()) return;

    createComment(
      { content },
      {
        onSuccess: () => {
          setContent("");
        },
      },
    );
  };

  return (
    <form onSubmit={handleSubmit} className="flex items-center gap-1">
      <Input
        placeholder={session ? "Add a comment..." : "Log in to comment"}
        className="w-full"
        disabled={!session || isPending}
        value={content}
        onChange={(e) => setContent(e.target.value)}
      />
      <Button
        aria-label="comment"
        variant="outline"
        disabled={!session || isPending || !content.trim()}
        type="submit"
      >
        <ICONS.send />
      </Button>
    </form>
  );
};
