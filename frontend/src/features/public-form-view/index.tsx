"use client";

import { Button } from "@/core/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/core/components/ui/dialog";
import { Input } from "@/core/components/ui/input";
import { Label } from "@radix-ui/react-label";
import {
  BadgeQuestionMark,
  ChevronDown,
  ClockArrowUp,
  HatGlasses,
  ListChecks,
  Lock,
  MoreHorizontal,
  PersonStanding,
} from "lucide-react";
import Image from "next/image";
import { useState } from "react";
import { Question } from "./components/question";

const questions = [
  {
    name: "Jaka jest rasa tego pięknego kota",
    imageKey: "",
    answers: [
      { content: "Opcja 1" },
      { content: "Opcja 2" },
      { content: "Opcja 3" },
      { content: "Opcja 4" },
    ],
  },
  {
    name: "Jaka jest rasa tego pięknego kota",
    imageKey: "1",
    answers: [
      { content: "Opcja 1" },
      { content: "Opcja 2" },
      { content: "Opcja 3" },
      { content: "Opcja 4" },
    ],
  },
];

export default function PublicFormView() {
  const [showQuestions, setShowQuestions] = useState(true);

  return (
    <div className="m-auto flex max-w-5xl flex-col">
      <div className="relative mb-[25px] flex h-[200px] w-full max-w-5xl items-end">
        <Image
          src="/banner.jpg"
          alt="Background"
          fill
          style={{ objectFit: "cover" }}
          priority
          className="-z-10 rounded-3xl"
        />
        <Button size={"icon-sm"} className="absolute top-4 right-4">
          <MoreHorizontal />
        </Button>
        <div className="absolute bottom-[-25px] left-4 h-[100px] w-[100px] rounded-full border-2 border-black bg-red-500"></div>
        <div className="absolute right-4 bottom-4 flex gap-2">
          <Dialog>
            <form>
              <DialogTrigger asChild>
                <Button size={"sm"}>
                  <ListChecks />
                  Wykonaj Quiz
                </Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Podaj hasło do quizu</DialogTitle>
                  <DialogDescription>
                    Ten quiz jest zabezpieczony hasłem, zapytaj twórcę quizu o
                    hasło
                  </DialogDescription>
                </DialogHeader>
                <div className="grid gap-3">
                  <Label htmlFor="code">Code</Label>
                  <Input id="code" name="code" placeholder="code" />
                </div>
                <DialogFooter>
                  <DialogClose asChild>
                    <Button variant="outline">Cancel</Button>
                  </DialogClose>
                  <Button type="submit">Save changes</Button>
                </DialogFooter>
              </DialogContent>
            </form>
          </Dialog>
          <Button size={"sm"}>
            <HatGlasses />
            Wykonaj Anonimowo
          </Button>
        </div>
      </div>
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
      <div className="my-2 flex flex-col gap-2">
        <div className="flex gap-2">
          <h1 className="text-xl">Pokaż pytania</h1>
          <Button
            size={"icon-sm"}
            onClick={() => setShowQuestions(!showQuestions)}
          >
            <ChevronDown />
          </Button>
        </div>
        {showQuestions &&
          questions.map((question, idx) => (
            <Question key={idx} {...question} />
          ))}
      </div>
    </div>
  );
}
