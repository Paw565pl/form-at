import { createParser } from "nuqs/server";

const allowed = [
  "newest",
  "oldest",
  "name",
  "questions",
  "submissions",
  "duration",
];

export const sortByParser = createParser<string>({
  parse(value) {
    return allowed.includes(value) ? value : allowed[0];
  },
  serialize(value) {
    return allowed.includes(value) ? value : allowed[0];
  },
});
