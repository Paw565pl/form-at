import { Logo } from "@/core/components/logo/logo";
import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";
import { useTranslations } from "next-intl";
import Link from "next/dist/client/link";
import Image from "next/image";

const stats = [
  {
    label: "Users",
    description:
      "Are you a form creator or a form filler? Join our community of users who are shaping the future of information gathering with formAT.",
    icon: ICONS.user,
    value: "123",
  },
  {
    label: "Forms",
    description:
      "What kind of bread are you? Which cat breed is the best? Explore the variety of forms created with formAT.",
    icon: ICONS.form,
    value: "123",
  },
  {
    label: "Submissions",
    description:
      "How many times have people hit submit? Discover the engagement and impact of forms created with formAT.",
    icon: ICONS.submissions,
    value: "456",
  },
];

const features = [
  {
    title: "Advanced Poll Maker",
    description:
      "Create complex forms with various question types. Make it public or private. Customize with shuffle options and add images and banners.",
    icon: ICONS.form,
    image: "/home1.png",
    reverse: false,
  },
  {
    title: "Live Results",
    description:
      "Watch your form submissions in real time. See how people are responding to your questions and get instant feedback and statistics.",
    icon: ICONS.submissions,
    image: "/home1.png",
    reverse: true,
  },
  {
    title: "Rate and Review",
    description:
      "Share your thoughts and opinions on forms created by others. Rate and review forms to help creators improve and to guide other users in finding the best forms for their needs.",
    icon: ICONS.rate,
    image: "/home1.png",
    reverse: false,
  },
];

export const Home = () => {
  const t = useTranslations("homePage");

  return (
    <section id="home" className="flex flex-col gap-16 p-10">
      <header className="flex flex-col items-center p-10">
        <div className="text-primary flex items-center text-6xl font-semibold">
          <Logo className="h-36 w-36" />
          <h1>formAT</h1>
        </div>
        <p className="text-muted-foreground mx-20 text-center text-lg">
          {t("appDescription")}
        </p>
      </header>

      <div className="flex justify-center gap-4">
        {stats.map((stat) => (
          <Card key={stat.label} className="flex-1 gap-2 p-6">
            <div className="flex items-center gap-2">
              <stat.icon className="text-primary h-6 w-6" />
              <h3 className="text-lg font-semibold">
                {stat.value} {stat.label}
              </h3>
            </div>
            <p className="text-muted-foreground text-sm">{stat.description}</p>
          </Card>
        ))}
      </div>

      {features.map((feature) => (
        <div
          key={feature.title}
          className={cn(
            "flex flex-col items-center justify-around gap-10 lg:flex-row",
            feature.reverse && "lg:flex-row-reverse",
          )}
        >
          <Card className="flex flex-col gap-4 p-6">
            <div className="flex items-center gap-2">
              <feature.icon className="text-primary h-6 w-6" />
              <h1 className="text-xl font-semibold">{feature.title}</h1>
            </div>
            <p className="text-muted-foreground">{feature.description}</p>
          </Card>
          <Image
            src={feature.image}
            alt={feature.title}
            width={600}
            height={300}
            className="rounded-md border shadow-sm"
          ></Image>
        </div>
      ))}

      <footer className="flex items-center gap-6 self-center">
        <h1 className="text-lg">Ready to get started?</h1>
        <Button asChild size="lg">
          <Link href="/forms/new">
            <ICONS.formNew />
            {t("createNewForm")}
          </Link>
        </Button>
      </footer>
    </section>
  );
};
