import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/core/components/ui/empty";
import {
  FormResponseDto,
  FormStatus,
  Language,
  ShuffleVariant,
} from "@/core/types/form";
import { QuestionType } from "@/core/types/question";
import { Banner } from "@/features/form-details/components/banner";
import { Details } from "@/features/form-details/components/details";
import { QuestionList } from "@/features/form-details/components/question-list";

import { MessageSquareX } from "lucide-react";
import { useTranslations } from "next-intl";

const form: FormResponseDto = {
  id: "1",
  name: "Formularz 1",
  slug: "formularz-1",
  description: "Opis formularza 1",
  language: Language.Pl,
  status: FormStatus.Public,
  shuffleVariant: ShuffleVariant.Null,
  thanksMessage: "Dziękujemy za wypełnienie formularza!",
  estimatedDuration: 5 * 1000 * 60,
  thumbnailKey: "thumbnail-1",
  allowsQuestionsPreview: true,
  allowsGuestSubmissions: false,
  saveSubmissions: true,
  authorId: "author-1",
  createdAt: new Date(),
  updatedAt: new Date(),
  submissionsCount: 0,
  questions: [
    {
      id: "1",
      content: "Jaka jest rasa tego pięknego kota",
      type: QuestionType.Single,
      imageKey: "",
      isRequired: true,
      answers: [
        { id: "q1a1", content: "Opcja 1", isCorrect: false },
        { id: "q1a2", content: "Opcja 2", isCorrect: false },
        { id: "q1a3", content: "Opcja 3", isCorrect: false },
        { id: "q1a4", content: "Opcja 4", isCorrect: false },
      ],
    },
    {
      id: "2",
      content: "Jaka jest rasa tego pięknego kota",
      type: QuestionType.Single,
      imageKey: "1",
      isRequired: true,
      answers: [
        { id: "q2a1", content: "Opcja 1", isCorrect: false },
        { id: "q2a2", content: "Opcja 2", isCorrect: false },
        { id: "q2a3", content: "Opcja 3", isCorrect: false },
        { id: "q2a4", content: "Opcja 4", isCorrect: false },
      ],
    },
  ],
};

const FormDetailsPage = () => {
  const t = useTranslations("publicFormView.questionList");

  return (
    <main className="mx-auto flex max-w-5xl flex-col items-center justify-center">
      <div className="w-full px-4">
        <Banner form={form} />

        <Details form={form} />

        {form.questions.length > 0 && form.allowsQuestionsPreview ? (
          <QuestionList questions={form.questions} />
        ) : (
          <Empty className="my-4 border border-dashed">
            <EmptyHeader>
              <EmptyMedia variant="icon">
                <MessageSquareX />
              </EmptyMedia>
              <EmptyTitle>{t("noQuestions")}</EmptyTitle>
              <EmptyDescription>{t("noQuestionsDescription")}</EmptyDescription>
            </EmptyHeader>
          </Empty>
        )}
      </div>
    </main>
  );
};

export default FormDetailsPage;
