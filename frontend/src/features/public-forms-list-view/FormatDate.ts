// To-do: Move this to an appropriate folder (core/tools maybe?)

const MONTHS_ENG = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];

const MONTHS_PL = [
  "stycznia",
  "lutego",
  "marca",
  "kwietnia",
  "maja",
  "czerwca",
  "lipca",
  "sierpnia",
  "września",
  "października",
  "listopada",
  "grudnia",
];

function GetSuffix(day: number): string {
  if (day > 3 && day < 21) return "th";
  switch (day % 10) {
    case 1:
      return "st";
    case 2:
      return "nd";
    case 3:
      return "rd";
    default:
      return "th";
  }
}

export function FormatDate(
  date: Date,
  show_month = false,
  show_time = false,
  lang = "en",
): string {
  date = new Date(date);
  const year = date.getFullYear();
  let str = "";
  if (show_month) {
    const day = String(date.getDate());
    const suffix = GetSuffix(date.getDate());
    const month =
      lang === "pl" ? MONTHS_PL[date.getMonth()] : MONTHS_ENG[date.getMonth()];
    str = `${day}${lang === "en" ? suffix : ""} ${month} ${year}`;
  } else {
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    str = `${day}-${month}-${year}`;
  }
  if (show_time) {
    str = str + " " + FormatTime(date);
  }
  return str;
}

export function FormatTime(date: Date): string {
  date = new Date(date);
  const hours = String(date.getUTCHours()).padStart(2, "0");
  const minutes = String(date.getUTCMinutes()).padStart(2, "0");
  return `${hours}:${minutes}`;
}
