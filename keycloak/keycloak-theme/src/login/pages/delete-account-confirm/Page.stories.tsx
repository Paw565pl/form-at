/**
 * This file has been claimed for ownership from @oussemasahbeni/keycloakify-login-shadcn version 250004.0.20.
 * To relinquish ownership and restore this file to its original content, run the following command:
 *
 * $ npx keycloakify own --path "login/pages/delete-account-confirm/Page.stories.tsx" --revert
 * claimed only to fix sync-extensions
 */

import {
  createKcPageStory,
  type Meta,
  type StoryObj,
} from "@/login/mocks/KcPageStory";

const { KcPageStory } = createKcPageStory({
  pageId: "delete-account-confirm.ftl",
});

const meta = {
  title: "login/delete-account-confirm.ftl",
  component: KcPageStory,
} satisfies Meta<typeof KcPageStory>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};

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
        rtl: false,
      },
    },
  },
};

export const WithAIAFlow: Story = {
  args: {
    kcContext: {
      triggered_from_aia: true,
      url: { loginAction: "/login-action" },
    },
  },
};
export const WithoutAIAFlow: Story = {
  args: {
    kcContext: {
      triggered_from_aia: false,
      url: { loginAction: "/login-action" },
    },
  },
};
export const WithCustomButtonStyle: Story = {
  args: {
    kcContext: {
      triggered_from_aia: true,
      url: { loginAction: "/login-action" },
    },
  },
};
