// @ts-check

import eslint from "@eslint/js";
import tanstackQueryPlugin from "@tanstack/eslint-plugin-query";
import nextVitals from "eslint-config-next/core-web-vitals";
import nextTs from "eslint-config-next/typescript";
import eslintConfigPrettier from "eslint-config-prettier/flat";
import noRelativeImportPaths from "eslint-plugin-no-relative-import-paths";
import eslintPluginZod from "eslint-plugin-zod";
import { defineConfig, globalIgnores } from "eslint/config";
import tseslint from "typescript-eslint";

export default defineConfig(
  nextVitals,
  nextTs,
  eslint.configs.recommended,
  tseslint.configs.strict,
  tseslint.configs.stylistic,
  tanstackQueryPlugin.configs["flat/recommended"],
  eslintPluginZod.configs.recommended,
  {
    plugins: {
      "no-relative-import-paths": noRelativeImportPaths,
    },
    rules: {
      "no-relative-import-paths/no-relative-import-paths": [
        "error",
        { rootDir: "src", prefix: "@", allowSameFolder: false },
      ],
    },
  },
  eslintConfigPrettier,
  globalIgnores([
    // Default ignores of eslint-config-next:
    ".next/**",
    "out/**",
    "build/**",
    "next-env.d.ts",
  ]),
);
