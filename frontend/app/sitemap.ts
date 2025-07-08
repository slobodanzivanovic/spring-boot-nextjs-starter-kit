import {siteConfig} from "@/config/site";
import {DEFAULT_LOCALE, LOCALES} from "@/i18n/routing";
import {MetadataRoute} from "next";

const siteUrl = siteConfig.url;

type ChangeFrequency =
  | "always"
  | "hourly"
  | "daily"
  | "weekly"
  | "monthly"
  | "yearly"
  | "never"
  | undefined;

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  // TODO: check this???
  const staticPaths = ["", "login", "register", "verify", "reset-password"];

  const pages = LOCALES.flatMap(locale => {
    return staticPaths.map(path => {
      let url = siteUrl;

      if (locale !== DEFAULT_LOCALE) {
        url += `/${locale}`;
      }

      if (path) {
        url += `/${path}`;
      }

      return {
        url,
        lastModified: new Date(),
        changeFrequency: "daily" as ChangeFrequency,
        priority: path === "" ? 1.0 : 0.8,
      };
    });
  });

  return [...pages];
}
