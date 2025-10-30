export interface Form {
  id: number;
  title: string;
  status: "Published" | "Draft";
  questions: number;
  submissions: number;
  date_created: Date;
  author: string;
  description: string;
  duration: number;
  image_url?: string;
}

export const placeholder_image_url =
  "https://media.gettyimages.com/id/2160237234/video/grass-waving-in-the-wind-sunny-day-in-the-forest.jpg?s=640x640&k=20&c=A5Fkny7OIkrfeouSyoUZ_m9vn0QkJd9oTv4Scs4iOfU=";

export const forms: Form[] = [
  {
    id: 1,
    title: "Quiz o kotach",
    status: "Published",
    questions: 3,
    submissions: 34,
    date_created: new Date("2021-10-25"),
    author: "Jan Kowalski",
    description: "Krótki quiz sprawdzający wiedzę o kotach.",
    image_url:
      "https://media.istockphoto.com/id/1038870630/photo/woman-standing-and-looking-at-lago-di-carezza-in-dolomites.jpg?s=612x612&w=0&k=20&c=sRbpFCJ-odpl2cQetrZaxlxL0oqAmxcjDEGN46kmGJ0=",
    duration: 5,
  },
  {
    id: 2,
    title: "Customer Satisfaction Survey",
    status: "Draft",
    questions: 10,
    submissions: 0,
    date_created: new Date("2023-09-23"),
    author: "Jane Smith",
    description:
      "A very long description that goes into detail about the purpose of the customer satisfaction survey form, its intended audience, and the types of questions that will be included to gather comprehensive feedback from customers regarding their experiences with our products and services.",
    image_url:
      "https://img.freepik.com/free-photo/morskie-oko-tatry_1204-510.jpg?semt=ais_hybrid&w=740&q=80",
    duration: 15,
  },
  {
    id: 3,
    title: "Event Feedback Form",
    status: "Published",
    questions: 5,
    submissions: 12,
    date_created: new Date("2023-08-02"),
    author: "Alice Johnson",
    description: "Collect feedback from event attendees.",
    duration: 10,
  },
  {
    id: 4,
    title: "Website Usability Survey",
    status: "Published",
    questions: 7,
    submissions: 36,
    date_created: new Date("2025-07-30"),
    author: "Bob Brown",
    description: "Feedback form for website usability studies.",
    image_url:
      "https://img.freepik.com/free-photo/beautiful-landscape-mountains-lake_74190-14081.jpg?w=740&t=st=1698247201~exp=1698247801~hmac=3f3f3f6f4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e4e",
    duration: 12,
  },
  {
    id: 5,
    title: "Product Order Form",
    status: "Draft",
    questions: 4,
    submissions: 0,
    date_created: new Date("2023-12-12"),
    author: "Charlie Davis",
    description: "Form to place product orders online.",
    duration: 8,
  },
  {
    id: 6,
    title: "Employee Satisfaction Survey",
    status: "Published",
    questions: 8,
    submissions: 20,
    date_created: new Date("2018-05-01"),
    author: "David Wilson",
    description: "A survey to assess employee satisfaction within the company.",
    duration: 13,
  },
  {
    id: 7,
    title: "Market Research Questionnaire",
    status: "Published",
    questions: 10,
    submissions: 50,
    date_created: new Date("2023-03-15"),
    author: "Emily Clark",
    description: "A questionnaire to gather market research data.",
    duration: 4,
  },
  {
    id: 8,
    title: "Event Registration Form",
    status: "Draft",
    questions: 6,
    submissions: 0,
    date_created: new Date("2023-02-28"),
    author: "Fiona Green",
    description: "Form for registering attendees for events.",
    duration: 18,
  },
];
