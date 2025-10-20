// @ts-check

import { includeIgnoreFile } from "@eslint/compat";
import { FlatCompat } from "@eslint/eslintrc";
import eslint from "@eslint/js";
import tanstackQueryPlugin from "@tanstack/eslint-plugin-query";
import eslintConfigPrettier from "eslint-config-prettier/flat";
import noRelativeImportPaths from "eslint-plugin-no-relative-import-paths";
import reactHooks from "eslint-plugin-react-hooks";
import { defineConfig } from "eslint/config";
import { fileURLToPath } from "node:url";
import { dirname } from "path";
import tseslint from "typescript-eslint";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const compat = new FlatCompat({
  baseDirectory: __dirname,
});

const gitignorePath = fileURLToPath(new URL(".gitignore", import.meta.url));

export default defineConfig(
  includeIgnoreFile(gitignorePath),
  ...compat.extends("next/core-web-vitals"),
  eslint.configs.recommended,
  tseslint.configs.strict,
  tseslint.configs.stylistic,
  reactHooks.configs.flat["recommended-latest"],
  ...tanstackQueryPlugin.configs["flat/recommended"],
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
);
