import { FormDetailResponseDto } from "@/core/types/form";

const shuffleArray = <T>(array: T[]): T[] => {
  const arr = [...array];
  for (let i = arr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [arr[i], arr[j]] = [arr[j] as T, arr[i] as T];
  }

  return arr;
};

export const shuffleFormData = (
  formData: FormDetailResponseDto,
): FormDetailResponseDto => {
  switch (formData.shuffleVariant) {
    case "QUESTIONS":
      return {
        ...formData,
        questions: shuffleArray(formData.questions),
      };
    case "ANSWERS":
      return {
        ...formData,
        questions: formData.questions.map((q) => ({
          ...q,
          answers: shuffleArray(q.answers),
        })),
      };
    case "ALL":
      return {
        ...formData,
        questions: shuffleArray(
          formData.questions.map((q) => ({
            ...q,
            answers: shuffleArray(q.answers),
          })),
        ),
      };
    default:
      return formData;
  }
};
