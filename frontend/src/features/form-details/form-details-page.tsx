import { FormResponseDto, FormStatus, Language } from "@/core/types/form";
import { QuestionType } from "@/core/types/question";
import { Banner } from "@/features/form-details/components/banner";
import { Details } from "@/features/form-details/components/details";
import { QuestionList } from "@/features/form-details/components/question/question-list";
import { useTranslations } from "next-intl";

const form: FormResponseDto = {
  id: "1",
  name: "Jak obszerna jest Twoja wiedza o kotach?",
  slug: "formularz-1",
  description:
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  language: Language.Pl,
  status: FormStatus.Private,
  thanksMessage: "Dziękujemy za wypełnienie formularza!",
  estimatedDuration: "PT5M",
  thumbnailKey: undefined,
  allowsQuestionsPreview: true,
  allowsGuestSubmissions: true,
  saveSubmissions: true,
  authorId: "author-1",
  createdAt: new Date(),
  updatedAt: new Date(),
  submissionsCount: 0,
  questions: [
    {
      id: "1",
      content: "Jaka jest rasa tego pięknego kota?",
      type: QuestionType.Single,
      imageKey: undefined,
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
      content: "Jakie rasy kotów widzisz na obrazku?",
      type: QuestionType.Multiple,
      imageKey: "1",
      isRequired: true,
      answers: [
        { id: "q2a1", content: "Opcja 1", isCorrect: false },
        { id: "q2a2", content: "Opcja 2", isCorrect: false },
        { id: "q2a3", content: "Opcja 3", isCorrect: false },
        { id: "q2a4", content: "Opcja 4", isCorrect: false },
      ],
    },
    {
      id: "3",
      content: "Jak wygląda Twój wymarzony kot?",
      type: QuestionType.Open,
      imageKey: "1",
      isRequired: false,
      answers: [],
    },
  ],
};

export const FormDetailsPage = () => {
  const t = useTranslations("formDetailsPage");

  return (
    <section id="form-details" className="px-5 py-10 lg:px-30">
      <Banner form={form} />
      <Details form={form} />

      {form.allowsQuestionsPreview ? (
        <QuestionList questions={form.questions} />
      ) : (
        <p className="text-muted-foreground p-4 text-sm">
          {t("questionList.noPreviewAllowed")}
        </p>
      )}
    </section>
  );
};
