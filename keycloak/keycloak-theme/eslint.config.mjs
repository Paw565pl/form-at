// @ts-check

import eslint from "@eslint/js";
import eslintConfigPrettier from "eslint-config-prettier/flat";
import noRelativeImportPaths from "eslint-plugin-no-relative-import-paths";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import storybook from "eslint-plugin-storybook";
import { defineConfig, globalIgnores } from "eslint/config";
import globals from "globals";
import tseslint from "typescript-eslint";

export default defineConfig(
  [
    globalIgnores(["dist"]),
    {
      files: ["**/*.{ts,tsx}"],
      extends: [
        eslint.configs.recommended,
        tseslint.configs.strict,
        tseslint.configs.stylistic,
        reactHooks.configs.flat.recommended,
        reactRefresh.configs.vite,
        storybook.configs["flat/recommended"],
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
      ],
      languageOptions: {
        globals: globals.browser,
      },
    },
  ],
  {
    linterOptions: {
      reportUnusedDisableDirectives: "off",
    },
  },
);
