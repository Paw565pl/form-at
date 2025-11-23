import { createParser } from "nuqs/server";
import { parse, serialize } from "tinyduration";

export const parseAsDuration = createParser({
  parse(value) {
    try {
      return serialize(parse(value));
    } catch {
      return null;
    }
  },
  serialize(value) {
    return value;
  },
});
