"use server";

import { cookies } from "next/headers";

export const switchLocale = async (locale: string) => {
  (await cookies()).set("locale", locale);
};
