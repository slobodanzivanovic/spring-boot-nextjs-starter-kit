import {Metadata} from "next";
import {getTranslations} from "next-intl/server";
import {constructMetadata} from "@/lib/metadata";
import {Locale} from "@/i18n/navigation";
import RegisterForm from "@/components/auth/RegisterForm";
import {LOCALES} from "@/i18n/routing";
import styles from "./Register.module.css";
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
  const t = await getTranslations({locale, namespace: "Register"});

  return constructMetadata({
    page: "Register",
    title: t("title"),
    description: t("description"),
    locale: locale as Locale,
    path: `/register`,
  });
}

export default async function Register() {
  return (
    <ProtectedRoute requireAuth={false} redirectTo="/">
      <div className={styles.container}>
        <RegisterForm />
      </div>
    </ProtectedRoute>
  );
}

export async function generateStaticParams() {
  return LOCALES.map(locale => ({
    locale,
  }));
}
