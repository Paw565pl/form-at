import { User } from "@/features/auth/types/user";

export enum FormStatus {
  DRAFT = "DRAFT",
  PUBLIC = "PUBLIC",
  UNPUBLIC = "UNPUBLIC",
  PRIVATE = "PRIVATE",
  CLOSED = "CLOSED",
}

export enum FormShuffleVariant {
  QUESTIONS = "QUESTIONS",
  ANSWERS = "ANSWERS",
  ALL = "ALL",
}

export interface FormEntity {
  id: string;
  name: string;
  slug: string;
  description?: string;
  status: FormStatus;
  shuffleVariant?: FormShuffleVariant;
  thanksMessage?: string;
  estimatedDuration: number;
  thumbnailKey?: string;
  allowsQuestionsPreview: boolean;
  allowsGuestSubmissions: boolean;
  saveSubmissions: boolean;
  submissionsCount: number;
  questionsCount?: number;
  author: User;
  // questions: QuestionEntity[];
  // submissions: SubmissionEntity[];
  createdAt: Date;
  updatedAt?: Date;
  version?: number;
}

export const placeholder_image_url =
  "https://media.gettyimages.com/id/2160237234/video/grass-waving-in-the-wind-sunny-day-in-the-forest.jpg?s=640x640&k=20&c=A5Fkny7OIkrfeouSyoUZ_m9vn0QkJd9oTv4Scs4iOfU=";

export const forms: FormEntity[] = [
  {
    id: "1",
    name: "Quiz o kotach",
    slug: "quiz-o-kotach",
    description: "Krótki quiz sprawdzający wiedzę o kotach.",
    status: FormStatus.PUBLIC,
    estimatedDuration: 5,
    thumbnailKey:
      "https://media.istockphoto.com/id/1038870630/photo/woman-standing-and-looking-at-lago-di-carezza-in-dolomites.jpg?s=612x612&w=0&k=20&c=sRbpFCJ-odpl2cQetrZaxlxL0oqAmxcjDEGN46kmGJ0=",
    allowsQuestionsPreview: true,
    allowsGuestSubmissions: true,
    saveSubmissions: true,
    submissionsCount: 34,
    questionsCount: 3,
    author: { id: "1", name: "Jan Kowalski", email: "", roles: [] },
    createdAt: new Date("2021-10-25"),
    updatedAt: new Date("2021-10-25"),
    version: 1,
  },
  {
    id: "2",
    name: "Customer Satisfaction Survey",
    slug: "customer-satisfaction-survey",
    description:
      "A very long description that goes into detail about the purpose of the customer satisfaction survey form, its intended audience, and the types of questions that will be included to gather comprehensive feedback from customers regarding their experiences with our products and services.",
    status: FormStatus.PUBLIC,
    estimatedDuration: 15,
    thumbnailKey:
      "https://img.freepik.com/free-photo/morskie-oko-tatry_1204-510.jpg?semt=ais_hybrid&w=740&q=80",
    allowsQuestionsPreview: true,
    allowsGuestSubmissions: false,
    saveSubmissions: true,
    submissionsCount: 0,
    questionsCount: 10,
    author: { id: "2", name: "Jane Smith", email: "", roles: [] },
    createdAt: new Date("2023-09-23"),
    updatedAt: new Date("2023-09-23"),
    version: 1,
  },
  {
    id: "3",
    name: "Event Feedback Form",
    slug: "event-feedback-form",
    description: "Collect feedback from event attendees.",
    status: FormStatus.PUBLIC,
    estimatedDuration: 10,
    thumbnailKey: placeholder_image_url,
    allowsQuestionsPreview: true,
    allowsGuestSubmissions: true,
    saveSubmissions: true,
    submissionsCount: 12,
    questionsCount: 5,
    author: { id: "3", name: "Alice Johnson", email: "", roles: [] },
    createdAt: new Date("2023-08-02"),
    updatedAt: new Date("2023-08-02"),
    version: 1,
  },
  {
    id: "4",
    name: "Website Usability Survey",
    slug: "website-usability-survey",
    description: "Feedback form for website usability studies.",
    status: FormStatus.PUBLIC,
    estimatedDuration: 12,
    thumbnailKey:
      "https://img.freepik.com/free-photo/beautiful-landscape-mountains-lake_74190-14081.jpg?w=740&t=st=1698247201~exp=1698247801~hmac=3f3f3f6f4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e",
    allowsQuestionsPreview: true,
    allowsGuestSubmissions: false,
    saveSubmissions: true,
    submissionsCount: 36,
    questionsCount: 7,
    author: { id: "4", name: "Bob Brown", email: "", roles: [] },
    createdAt: new Date("2025-07-30"),
    updatedAt: new Date("2025-07-30"),
    version: 1,
  },
  {
    id: "5",
    name: "Product Order Form",
    slug: "product-order-form",
    description: "Form to place product orders online.",
    status: FormStatus.PUBLIC,
    estimatedDuration: 8,
    thumbnailKey: placeholder_image_url,
    allowsQuestionsPreview: false,
    allowsGuestSubmissions: true,
    saveSubmissions: true,
    submissionsCount: 0,
    questionsCount: 4,
    author: { id: "5", name: "Charlie Davis", email: "", roles: [] },
    createdAt: new Date("2023-12-12"),
    updatedAt: new Date("2023-12-12"),
    version: 1,
  },
  {
    id: "6",
    name: "Employee Satisfaction Survey",
    slug: "employee-satisfaction-survey",
    description: "A survey to assess employee satisfaction within the company.",
    status: FormStatus.PUBLIC,
    estimatedDuration: 13,
    thumbnailKey: placeholder_image_url,
    allowsQuestionsPreview: true,
    allowsGuestSubmissions: false,
    saveSubmissions: true,
    submissionsCount: 20,
    questionsCount: 8,
    author: { id: "6", name: "David Wilson", email: "", roles: [] },
    createdAt: new Date("2018-05-01"),
    updatedAt: new Date("2018-05-01"),
    version: 1,
  },
  {
    id: "7",
    name: "Market Research Questionnaire",
    slug: "market-research-questionnaire",
    description: "A questionnaire to gather market research data.",
    status: FormStatus.PUBLIC,
    estimatedDuration: 4,
    thumbnailKey: placeholder_image_url,
    allowsQuestionsPreview: false,
    allowsGuestSubmissions: false,
    saveSubmissions: true,
    submissionsCount: 50,
    questionsCount: 10,
    author: { id: "7", name: "Emily Clark", email: "", roles: [] },
    createdAt: new Date("2023-03-15"),
    updatedAt: new Date("2023-03-15"),
    version: 1,
  },
  {
    id: "8",
    name: "Event Registration Form",
    slug: "event-registration-form",
    description: "Form for registering attendees for events.",
    status: FormStatus.PUBLIC,
    estimatedDuration: 18,
    thumbnailKey: placeholder_image_url,
    allowsQuestionsPreview: true,
    allowsGuestSubmissions: true,
    saveSubmissions: true,
    submissionsCount: 0,
    questionsCount: 6,
    author: { id: "8", name: "Fiona Green", email: "", roles: [] },
    createdAt: new Date("2023-02-28"),
    updatedAt: new Date("2023-02-28"),
    version: 1,
  },
];
