export type AuthorsConfig = {
  name: string;
  url: string;
};

// export type ThemeColors = {
//   media: string;
//   color: string;
// }

export type SiteConfig = {
  name: string;
  tagLine: string;
  description: string;
  url: string;
  authors: AuthorsConfig[];
  socialLinks: {
    github: string;
    twitter: string;
    email: string;
  };
  creator: string;
  // themeColors?: string | ThemeColors[];
  // defaultNextTheme?: string;
  icons: {
    icon: string;
    shortcut?: string;
    apple?: string;
  };
};
