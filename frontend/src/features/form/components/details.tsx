import {
  BadgeQuestionMark,
  ClockArrowUp,
  Lock,
  PersonStanding,
} from "lucide-react";
import { useTranslations } from "next-intl";

export const Details = () => {
  const t = useTranslations("PublicFormView.Details");
  return (
    <div>
      <div className="my-2 flex justify-between">
        <div className="flex items-center gap-2">
          <h1 className="text-4xl font-extrabold">Quiz o kotach</h1>
          <Lock />
        </div>
        <div className="inline-flex h-9 w-min items-center justify-center rounded-xl border-2 border-gray-600 bg-blue-500 px-4 py-2 whitespace-nowrap text-white">
          {t("quizFinished", { finishedAt: "20.12.2025" })}
        </div>
      </div>
      <div className="flex gap-6 text-gray-500">
        <div className="flex items-center gap-1">
          <BadgeQuestionMark />
          <h2>{t("questionsCount", { count: "12" })}</h2>
        </div>
        <div className="flex items-center gap-1">
          <PersonStanding />
          <h2>{t("submissionsCount", { count: "1020" })}</h2>
        </div>
        <div className="flex items-center gap-1">
          <ClockArrowUp />
          <h2>{t("estimatedTime", { time: "5" })}</h2>
        </div>
      </div>
      <p>
        Fajniutki quiz o kotach z dosyć dłuższym opisem Fajniutki quiz o kotach
        z dosyć dłuższym opisem Fajniutki quiz o kotach z dosyć dłuższym opisem
        Fajniutki quiz o kotach z dosyć dłuższym opisem Fajniutki quiz o kotach
        z dosyć dłuższym opisem Fajniutki quiz o kotach z dosyć dłuższym opisem
        Fajniutki quiz o kotach z dosyć dłuższym opisem Fajniutki quiz o kotach
        z dosyć dłuższym opisem Fajniutki quiz o kotach z dosyć dłuższym opisem
        Fajniutki quiz o kotach z dosyć dłuższym opisem Fajniutki quiz o kotach
        z dosyć dłuższym opisem Fajniutki quiz o kotach z dosyć dłuższym opisem
      </p>
      <p className="w-full text-right text-gray-500">20.10.2025</p>
    </div>
  );
};
