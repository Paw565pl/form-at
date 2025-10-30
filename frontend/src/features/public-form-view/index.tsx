import { Banner } from "./components/banner";
import { Details } from "./components/details";
import { QuestionList } from "./components/question-list";
import { QuestionResponseDto } from "./types/question-response-dto";
import { QuestionType } from "./types/question-type";

const questions: QuestionResponseDto[] = [
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
];

export default function PublicFormView() {
  return (
    <div className="m-auto flex max-w-5xl flex-col">
      <Banner />
      <Details />
      <QuestionList questions={questions} />
    </div>
  );
}
