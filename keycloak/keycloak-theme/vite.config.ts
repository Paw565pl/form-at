import tailwindcss from "@tailwindcss/vite";
import react from "@vitejs/plugin-react";
import { keycloakify } from "keycloakify/vite-plugin";
import path from "node:path";
import { defineConfig } from "vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    keycloakify({
      accountThemeImplementation: "none",
      keycloakVersionTargets: {
        "22-to-25": false,
        "all-other-versions": "theme.jar",
      },
    }),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
});
