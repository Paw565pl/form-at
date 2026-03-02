"use client";

import { Logo } from "@/core/components/logo/logo";
import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";
import { useFetchStatistics } from "@/features/home/hooks/use-fetch-statistics";
import { useTranslations } from "next-intl";
import Link from "next/dist/client/link";
import Image from "next/image";
import { useEffect, useState } from "react";

const useCountUp = (target: number, duration = 1000) => {
  const [value, setValue] = useState(0);
  useEffect(() => {
    let start = 0;
    const increment = target / (duration / 16);
    const interval = setInterval(() => {
      start += increment;
      if (start >= target) {
        setValue(target);
        clearInterval(interval);
      } else {
        setValue(Math.floor(start));
      }
    }, 16);
    return () => clearInterval(interval);
  }, [target, duration]);
  return value;
};

const features = [
  {
    label: "advancedFormBuilder",
    icon: ICONS.form,
    image: "/images/home1.png",
    reverse: false,
  },
  {
    label: "liveResults",
    icon: ICONS.submissions,
    image: "/images/home2.png",
    reverse: true,
  },
  {
    label: "rateAndReview",
    icon: ICONS.rate,
    image: "/images/home3.png",
    reverse: false,
  },
] as const;

export const Home = () => {
  const t = useTranslations("homePage");
  const { data: statistics } = useFetchStatistics();

  const stats = [
    {
      label: "users",
      icon: ICONS.user,
      value: statistics?.usersCount ?? 0,
    },
    {
      label: "forms",
      icon: ICONS.form,
      value: statistics?.formsCount ?? 0,
    },
    {
      label: "submissions",
      icon: ICONS.submissions,
      value: statistics?.submissionsCount ?? 0,
    },
  ] as const;

  return (
    <section id="home" className="flex flex-col gap-16 p-10">
      <header className="flex flex-col items-center lg:p-12">
        <div className="text-primary flex items-center text-4xl font-semibold lg:text-6xl">
          <Logo className="size-20 lg:size-36" />
          <h1>formAT</h1>
        </div>
        <p className="text-muted-foreground text-center text-lg lg:mx-20">
          {t("appDescription")}
        </p>
      </header>

      <div className="flex flex-col justify-center gap-6 lg:mb-12 lg:flex-row">
        {stats.map((stat) => {
          // eslint-disable-next-line react-hooks/rules-of-hooks
          const animatedValue = useCountUp(stat.value);
          return (
            <Card key={stat.label} className="flex-1 gap-2 p-6">
              <div className="flex items-center gap-2">
                <stat.icon className="text-primary h-6 w-6" />
                <h3 className="text-lg font-semibold">
                  {t(`stats.${stat.label}`)}:{" "}
                  <span className="text-primary">{animatedValue}</span>
                </h3>
              </div>
              <p className="text-muted-foreground text-sm">
                {t(`stats.${stat.label}Desc`)}
              </p>
            </Card>
          );
        })}
      </div>

      {features.map((feature) => (
        <div
          key={feature.label}
          className={cn(
            "flex flex-col items-center justify-around gap-6 lg:flex-row lg:gap-10",
            feature.reverse && "lg:flex-row-reverse",
          )}
        >
          <Card className="flex flex-col gap-4 p-6">
            <div className="flex items-center gap-2">
              <feature.icon className="text-primary h-6 w-6" />
              <h1 className="text-xl font-semibold">
                {t(`features.${feature.label}`)}
              </h1>
            </div>
            <p className="text-muted-foreground">
              {t(`features.${feature.label}Desc`)}
            </p>
          </Card>
          <Image
            src={feature.image}
            alt={t(`features.${feature.label}`)}
            width={700}
            height={400}
            className="rounded-md border shadow-sm"
            priority
          ></Image>
        </div>
      ))}

      <footer className="flex flex-col items-center gap-4 self-center md:flex-row">
        <h1 className="text-muted-foreground text-lg">{t("getStarted")}</h1>
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
