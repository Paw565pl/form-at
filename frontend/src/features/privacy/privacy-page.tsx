import { Card } from "@/core/components/ui/card";
import { getTranslations } from "next-intl/server";

const privacyPolicy = [
  {
    title: "Introduction",
    content:
      "We value your privacy and are committed to protecting your personal information. This privacy policy explains how we collect, use, and safeguard your data when you use our services. By using our services, you agree to the terms outlined in this policy.",
  },
  {
    title: "Data Collection",
    content:
      "We collect personal information that you voluntarily provide to us when you register, create or fill out forms, leave comments, or interact with our services. This may include your name, email address, and any other information you choose to share.",
  },
  {
    title: "Use of Data",
    content:
      "We use the collected data solely to provide our services, including authentication, session management, providing core functionalities. We do not sell, rent, or share your personal information with third parties.",
  },
  {
    title: "Data Security",
    content:
      "We implement appropriate security measures to protect your personal information from unauthorized access, alteration, disclosure, or destruction. Data is retained only for as long as necessary to provide our services.",
  },
  {
    title: "Cookies",
    content:
      "We use cookies and similar technologies for personalization and authentication purposes. You can manage your cookie preferences at any time through your browser settings. No tracking or advertising cookies are used.",
  },
  {
    title: "Changes to This Policy",
    content:
      "We may update this Privacy Policy from time to time. Changes will be posted on this page.",
  },
];

export const PrivacyPage = async () => {
  const t = await getTranslations("privacyPage");
  return (
    <section id="privacy" className="px-5 py-10 lg:px-30">
      <Card className="flex w-full flex-col justify-between gap-4 p-4">
        <h1 className="text-2xl">{t("title")}</h1>
        <p>{t("subtitle")}</p>

        {privacyPolicy.map((item, index) => (
          <div key={index} className="flex flex-col gap-1">
            <h2 className="text-muted-foreground">
              {index + 1}. {item.title}
            </h2>
            <p>{item.content}</p>
          </div>
        ))}
      </Card>
    </section>
  );
};
