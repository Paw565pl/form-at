import en from "@/../messages/en.json";
import z from "zod";

export type ErrorKey = keyof typeof en.formDetailsPage.comments;

export const commentSchema = z.object({
  content: z.string().min(3, "commentContentMin").max(500, "commentContentMax"),
});
