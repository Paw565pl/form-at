import ky from "ky";

export const apiService = ky.create({
  prefixUrl: "",
});
