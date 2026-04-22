/**
 * This file has been claimed for ownership from @oussemasahbeni/keycloakify-login-shadcn version 250004.0.20.
 * To relinquish ownership and restore this file to its original content, run the following command:
 *
 * $ npx keycloakify own --path "login/pages/link-idp-action/Page.stories.tsx" --revert
 * claimed only to fix sync-extensions
 */

import {
  createKcPageStory,
  type Meta,
  type StoryObj,
} from "@/login/mocks/KcPageStory";

const { KcPageStory } = createKcPageStory({ pageId: "link-idp-action.ftl" });

const meta = {
  title: "login/link-idp-action.ftl",
  component: KcPageStory,
} satisfies Meta<typeof KcPageStory>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    kcContext: {
      idpDisplayName: "GitHub",
      url: { loginAction: "/mock-login-action" },
    },
  },
};

export const Arabic: Story = {
  args: {
    kcContext: {
      locale: {
        currentLanguageTag: "ar",
        rtl: true,
      },
    },
  },
};
export const French: Story = {
  args: {
    kcContext: {
      locale: {
        currentLanguageTag: "fr",
      },
    },
  },
};

export const DifferentProvider: Story = {
  args: {
    kcContext: {
      idpDisplayName: "Google",
      url: { loginAction: "/custom-login-action" },
    },
  },
};
