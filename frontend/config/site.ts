import {SiteConfig} from "@/types/site-config";

export const BASE_URL =
  process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:3000";

// TODO: update this with real values
const TWITTER_URL = "https://x.com/starter-kit";
const GITHUB_URL = "https://github.com/starter-kit";
const EMAIL_URL = "mailto:info@starter-kit.com";

// TODO: add other values here and reference in metadata.ts
export const siteConfig: SiteConfig = {
  name: "Starter Kit",
  tagLine: "A starter kit for fullstack apps",
  description: "A starter kit for fullstack apps",
  url: BASE_URL,
  authors: [
    {
      name: "Slobodan Zivanovic",
      url: "slobodan.zivanovic@tuta.com",
    },
  ],
  creator: "@slobodanzivanovic",
  socialLinks: {
    twitter: TWITTER_URL,
    github: GITHUB_URL,
    email: EMAIL_URL,
  },
  // themeColors: [
  //   {media: "(prefers-color-scheme: light)", color: "white"},
  //   {media: "(prefers-color-scheme: dark)", color: "black"},
  // ],
  // defaultNextTheme: "system", // next-theme option: system | dark | light
  icons: {
    icon: "/favicon.ico",
    shortcut: "/logo.png",
    apple: "/logo.png", // apple-touch-icon.png
  },
};
