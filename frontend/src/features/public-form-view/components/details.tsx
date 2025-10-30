import {
  BadgeQuestionMark,
  ClockArrowUp,
  Lock,
  PersonStanding,
} from "lucide-react";
export const Details = () => {
  return (
    <div>
      <div className="my-2 flex justify-between">
        <div className="flex items-center gap-2">
          <h1 className="text-4xl font-extrabold">Quiz o kotach</h1>
          <Lock className="text-gray-500" />
        </div>
        <div className="inline-flex h-9 w-min items-center justify-center rounded-xl border-2 border-gray-600 bg-blue-500 px-4 py-2 whitespace-nowrap text-white">
          Quiz wykonany: 20.12.2025
        </div>
      </div>
      <div className="flex gap-6 text-gray-500">
        <div className="flex items-center gap-1">
          <BadgeQuestionMark />
          <h2>12 pytań</h2>
        </div>
        <div className="flex items-center gap-1">
          <PersonStanding />
          <h2>1020 odpowiedzi</h2>
        </div>
        <div className="flex items-center gap-1">
          <ClockArrowUp />
          <h2>Szacunkowy czas wykonania: 5 minut</h2>
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
