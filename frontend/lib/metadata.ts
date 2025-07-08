import {siteConfig} from "@/config/site";
import {DEFAULT_LOCALE, LOCALE_NAMES} from "@/i18n/routing";
import {Locale} from "@/i18n/navigation";
import {Metadata} from "next";
import {getTranslations} from "next-intl/server";

type MetadataProps = {
  page?: string;
  title?: string;
  description?: string;
  images?: string[];
  noIndex?: boolean;
  locale: Locale;
  path?: string;
  canonicalUrl?: string;
};

export async function constructMetadata({
  page = "Home",
  title,
  description,
  images = [],
  noIndex = false,
  locale,
  path,
  canonicalUrl,
}: MetadataProps): Promise<Metadata> {
  const t = await getTranslations({locale, namespace: "Home"});

  // page specific metadata translations
  const pageTitle = title || t(`title`);
  const pageDescription = description || t(`description`);

  // build full title
  const finalTitle =
    page === "Home"
      ? `${pageTitle} - ${t("tagLine")}`
      : `${pageTitle} | ${t("title")}`;

  // build image URLs
  const imageUrls =
    images.length > 0
      ? images.map(img => ({
          url: img.startsWith("http") ? img : `${siteConfig.url}/${img}`,
          alt: pageTitle,
        }))
      : [
          {
            url: `${siteConfig.url}/og.png`,
            alt: pageTitle,
          },
        ];

  // Open Graph Site
  let pageURLPath = path || "";
  if (pageURLPath.startsWith("/")) {
    pageURLPath = pageURLPath.substring(1);
  }

  // build alternate language links
  const localePart = locale === DEFAULT_LOCALE ? "" : `/${locale}`;
  const pageURL = `${localePart}${pageURLPath ? `/${pageURLPath}` : ""}`;

  const alternateLanguages = Object.keys(LOCALE_NAMES).reduce(
    (acc, lang) => {
      let alternatePath = "";

      if (lang !== DEFAULT_LOCALE) {
        alternatePath = `/${lang}`;
      }

      if (canonicalUrl) {
        if (canonicalUrl.startsWith("/")) {
          alternatePath += canonicalUrl;
        } else {
          alternatePath += `/${canonicalUrl}`;
        }
      }

      acc[lang] = `${siteConfig.url}${alternatePath}`;
      return acc;
    },
    {} as Record<string, string>,
  );

  let canonicalUrlFull = undefined;
  if (canonicalUrl) {
    canonicalUrlFull = canonicalUrl.startsWith("/")
      ? `${siteConfig.url}${canonicalUrl}`
      : `${siteConfig.url}/${canonicalUrl}`;
  }

  return {
    title: finalTitle,
    description: pageDescription,
    keywords: [],
    authors: siteConfig.authors,
    creator: siteConfig.creator,
    metadataBase: new URL(siteConfig.url),
    alternates: {
      canonical: canonicalUrlFull,
      languages: alternateLanguages,
    },
    openGraph: {
      type: "website",
      title: finalTitle,
      description: pageDescription,
      url: `${siteConfig.url}${pageURL}`,
      siteName: t("title"),
      locale: locale,
      images: imageUrls,
    },
    twitter: {
      card: "summary_large_image",
      title: finalTitle,
      description: pageDescription,
      site: `${siteConfig.url}${pageURL}`,
      images: imageUrls,
      creator: siteConfig.creator,
    },
    robots: {
      index: !noIndex,
      follow: !noIndex,
      googleBot: {
        index: !noIndex,
        follow: !noIndex,
      },
    },
  };
}
