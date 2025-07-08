import HomeIndex from "@/components/pages/HomeIndex";
import {LOCALES} from "@/i18n/routing";

export default function Home() {
  return <HomeIndex />;
}

export async function generateStaticParams() {
  return LOCALES.map(locale => ({
    locale,
  }));
}
