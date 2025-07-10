import {DEFAULT_LOCALE} from "@/i18n/routing";
import {Locale} from "@/i18n/navigation";
import Cookies from "js-cookie";

const LOCALE_COOKIE_NAME = "NEXT_LOCALE";

export function getLocale(): string {
  const cookieLocale = Cookies.get(LOCALE_COOKIE_NAME);

  if (cookieLocale) {
    return cookieLocale;
  }

  if (typeof window !== "undefined") {
    const htmlLang = document.documentElement.lang;
    if (htmlLang) {
      return htmlLang;
    }
  }

  return DEFAULT_LOCALE;
}

export function setLocale(locale: Locale): void {
  Cookies.set(LOCALE_COOKIE_NAME, locale, {
    expires: 365,
    path: "/",
    sameSite: "lax",
    secure: window.location.protocol === "https:",
  });
}
