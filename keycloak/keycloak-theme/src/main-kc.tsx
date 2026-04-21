import { KcPage } from "@/kc.gen";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

if (!window.kcContext) {
  throw new Error("No Keycloak context");
}

// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <KcPage kcContext={window.kcContext} />
  </StrictMode>,
);
