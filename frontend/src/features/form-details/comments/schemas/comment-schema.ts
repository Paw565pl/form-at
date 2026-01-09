import en from "@root/messages/en.json";
import z from "zod";

export type ErrorKey = keyof typeof en.formDetailsPage.comments;

export const commentSchema = z.object({
  content: z.string().trim().min(3, "contentMin").max(500, "contentMax"),
});
