import {getLocale} from "@/lib/locale";
import Axios from "axios";
import {getToken, isTokenExpired, removeToken} from "@/lib/auth/token";
import {API_BASE_URL} from "@/constants/api/apiConfig";

let isRedirectingToLogin = false;

const httpClient = Axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "X-Requested-With": "XMLHttpRequest",
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
  xsrfCookieName: "XSRF-TOKEN",
  withXSRFToken: true,
  timeout: 10000,
});

httpClient.interceptors.request.use(
  async config => {
    config.headers["Accept-Language"] = getLocale();

    const token = getToken();

    if (token && !isTokenExpired()) {
      config.headers["Authorization"] = `Bearer ${token}`;
    } else if (token && isTokenExpired()) {
      removeToken();
    }

    return config;
  },
  error => {
    return Promise.reject(error);
  },
);

httpClient.interceptors.response.use(
  response => {
    return response;
  },
  async error => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      removeToken();

      if (
        typeof window !== "undefined" &&
        !isRedirectingToLogin &&
        !originalRequest.url.includes("/login") &&
        !originalRequest.url.includes("/api/v1/auth")
      ) {
        isRedirectingToLogin = true;

        setTimeout(() => {
          window.location.href = "/login";
          setTimeout(() => {
            isRedirectingToLogin = false;
          }, 5000);
        }, 100);
      }
    }

    return Promise.reject(error);
  },
);

export default httpClient;
