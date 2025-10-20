import axios from "axios";

export const apiService = axios.create({
  baseURL: "",
  timeout: 10_000,
  adapter: "fetch",
});
