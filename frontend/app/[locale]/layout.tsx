import {hasLocale, NextIntlClientProvider} from "next-intl";
import {getMessages, getTranslations} from "next-intl/server";
import {Metadata} from "next";
import {notFound} from "next/navigation";
import {DEFAULT_LOCALE, routing} from "@/i18n/routing";
import {Locale} from "@/i18n/navigation";
import {constructMetadata} from "@/lib/metadata";
import {Analytics} from "@vercel/analytics/next";
import "@/styles/globals.css";

type MetadataProps = {
  params: Promise<{locale: string}>;
};

export async function generateMetadata({
  params,
}: MetadataProps): Promise<Metadata> {
  const {locale} = await params;
  const t = await getTranslations({locale, namespace: "Home"});

  return constructMetadata({
    page: "Home",
    title: t("title"),
    description: t("description"),
    locale: locale as Locale,
    path: `/`,
  });
}

export default async function RootLayout({
  children,
  params,
}: {
  children: React.ReactNode;
  params: Promise<{locale: string}>;
}) {
  const {locale} = await params;
  if (!hasLocale(routing.locales, locale)) {
    notFound();
  }

  const messages = await getMessages();

  return (
    <html lang={locale || DEFAULT_LOCALE} suppressHydrationWarning>
      <head />
      <body>
        <NextIntlClientProvider messages={messages}>
          {children}
        </NextIntlClientProvider>

        {process.env.NODE_ENV === "development" ? (
          <></>
        ) : (
          <>
            <Analytics />
          </>
        )}
      </body>
    </html>
  );
}
