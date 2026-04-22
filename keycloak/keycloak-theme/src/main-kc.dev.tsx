import { KcPage } from "@/kc.gen";
import { getKcContextMock } from "@/login/mocks/getKcContextMock";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

const kcContext = getKcContextMock({
  pageId: "login.ftl",
  overrides: {},
});

// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <KcPage kcContext={kcContext} />
  </StrictMode>,
);
