import { FormResponseDto, FormStatus, Language } from "@/core/types/form";
import { QuestionType } from "@/core/types/question";
import { Banner } from "@/features/form-details/components/banner";
import { Details } from "@/features/form-details/components/details";
import { NoQuestions } from "@/features/form-details/components/question/no-questions";
import { QuestionList } from "@/features/form-details/components/question/question-list";

const form: FormResponseDto = {
  id: "1",
  name: "Formularz 1",
  slug: "formularz-1",
  description: "Opis formularza 1",
  language: Language.Pl,
  status: FormStatus.Public,
  thanksMessage: "Dziękujemy za wypełnienie formularza!",
  estimatedDuration: "PT6H5M",
  thumbnailKey: undefined,
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
  return (
    <main className="flex justify-center px-5 py-10 lg:px-30">
      <div className="flex w-full flex-col items-center justify-center px-4">
        <Banner form={form} />

        <Details form={form} />

        {form.questions.length > 0 && form.allowsQuestionsPreview ? (
          <QuestionList questions={form.questions} />
        ) : (
          <NoQuestions />
        )}
      </div>
    </main>
  );
};

export default FormDetailsPage;
