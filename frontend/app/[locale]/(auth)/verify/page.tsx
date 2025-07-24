import {Metadata} from "next";
import {getTranslations} from "next-intl/server";
import {constructMetadata} from "@/lib/metadata";
import {Locale} from "@/i18n/navigation";
import VerifyForm from "@/components/auth/VerifyForm";
import {LOCALES} from "@/i18n/routing";
import styles from "./Verify.module.css";
import ProtectedRoute from "@/components/auth/ProtectedRoute";

type Params = Promise<{
  locale: string;
}>;

type MetadataProps = {
  params: Params;
};

export async function generateMetadata({
  params,
}: MetadataProps): Promise<Metadata> {
  const {locale} = await params;
  const t = await getTranslations({locale, namespace: "Verify"});

  return constructMetadata({
    page: "Verify",
    title: t("title"),
    description: t("description"),
    locale: locale as Locale,
    path: `/verify`,
  });
}

export default async function Verify() {
  return (
    <ProtectedRoute requireAuth={false} redirectTo="/">
      <div className={styles.container}>
        <VerifyForm />
      </div>
    </ProtectedRoute>
  );
}

export async function generateStaticParams() {
  return LOCALES.map(locale => ({
    locale,
  }));
}
