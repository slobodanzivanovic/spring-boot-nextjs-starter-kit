import {defineRouting} from "next-intl/routing";

export const LOCALES = ["sr", "en"];
export const DEFAULT_LOCALE = "sr";
export const LOCALE_NAMES: Record<string, string> = {
  sr: "Serbian",
  en: "English",
};

export const routing = defineRouting({
  locales: LOCALES,

  defaultLocale: DEFAULT_LOCALE,

  localeDetection: false, // We should maybe get this from .env file??

  localePrefix: "as-needed",
});
