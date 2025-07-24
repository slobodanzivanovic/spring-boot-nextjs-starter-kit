import Container from "@/components/common/Container";
import {siteConfig} from "@/config/site";
import {Locale} from "@/i18n/navigation";
import {DEFAULT_LOCALE, routing} from "@/i18n/routing";
import {constructMetadata} from "@/lib/metadata";
import "@/styles/globals.css";
import {Analytics} from "@vercel/analytics/next";
import {Metadata, Viewport} from "next";
import {hasLocale, NextIntlClientProvider} from "next-intl";
import {getMessages, getTranslations} from "next-intl/server";
import {ThemeProvider} from "next-themes";
import {notFound} from "next/navigation";
import {ToastProvider} from "@/components/common";
import {AuthProvider} from "@/lib/auth/AuthContext";

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

export const viewport: Viewport = {
  themeColor: siteConfig.themeColors,
};

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
          <ThemeProvider
            attribute="class"
            defaultTheme={siteConfig.defaultNextTheme}
            enableSystem
          >
            <AuthProvider>
              <ToastProvider
                defaultPosition="bottom-right"
                defaultDuration={5000}
              >
                <Container size="large">{children}</Container>
              </ToastProvider>
            </AuthProvider>
          </ThemeProvider>
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
