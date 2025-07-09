import type {NextConfig} from "next";
import createNextIntlPlugin from "next-intl/plugin";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "s3.tebi.io",
        pathname: "/programiraj/**",
      },
      {
        protocol: "https",
        hostname: "avatars.githubusercontent.com",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "lh3.googleusercontent.com",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "i.pravatar.cc",
        pathname: "/**",
      },
    ],
  },
  compiler: {
    removeConsole:
      process.env.NODE_ENV === "production" ? {exclude: ["error"]} : false,
  },
};

const withNextIntl = createNextIntlPlugin();

export default withNextIntl(nextConfig);
