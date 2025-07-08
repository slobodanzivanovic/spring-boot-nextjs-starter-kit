import createMiddleware from "next-intl/middleware";
import {routing} from "@/i18n/routing";
import {NextRequest, NextResponse} from "next/server";

const LOCALE_COOKIE_NAME = "NEXT_LOCALE";

const intlMiddleware = createMiddleware(routing);

export default async function middleware(request: NextRequest) {
  const localeCookie = request.cookies.get(LOCALE_COOKIE_NAME)?.value;
  const pathname = request.nextUrl.pathname;

  // check if pathname already has a locale
  const pathnameHasLocale = routing.locales.some(
    locale => pathname.startsWith(`/${locale}/`) || pathname === `/${locale}`,
  );

  // if no locale in path and we have a cookie with non-default locale, redirect
  if (
    !pathnameHasLocale &&
    localeCookie &&
    localeCookie !== routing.defaultLocale &&
    routing.locales.includes(localeCookie)
  ) {
    const url = new URL(request.url);
    url.pathname = `/${localeCookie}${pathname}`;
    return NextResponse.redirect(url);
  }

  // if path has locale but cookie doesn't match, update cookie
  if (pathnameHasLocale) {
    const currentLocale = pathname.split("/")[1];
    const response = intlMiddleware(request);

    if (
      currentLocale &&
      localeCookie !== currentLocale &&
      routing.locales.includes(currentLocale)
    ) {
      response.cookies.set(LOCALE_COOKIE_NAME, currentLocale, {
        path: "/",
        maxAge: 60 * 60 * 24 * 365,
        sameSite: "lax",
        secure: process.env.NODE_ENV === "production",
        httpOnly: false,
      });
    }

    return response;
  }

  // if no locale in path and cookie is default locale (or no cookie), let next-intl handle it
  return intlMiddleware(request);
}

export const config = {
  matcher: "/((?!api|trpc|_next|_vercel|.*\\..*).*)",
};
