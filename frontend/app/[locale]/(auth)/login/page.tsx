import {Metadata} from "next";
import {getTranslations} from "next-intl/server";
import {constructMetadata} from "@/lib/metadata";
import {Locale} from "@/i18n/navigation";
import LoginForm from "@/components/auth/LoginForm";
import {LOCALES} from "@/i18n/routing";
import styles from "./Login.module.css";
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
  const t = await getTranslations({locale, namespace: "Login"});

  return constructMetadata({
    page: "Login",
    title: t("title"),
    description: t("description"),
    locale: locale as Locale,
    path: `/login`,
  });
}

export default async function Login() {
  return (
    <ProtectedRoute requireAuth={false} redirectTo="/">
      <div className={styles.container}>
        <LoginForm />
      </div>
    </ProtectedRoute>
  );
}

export async function generateStaticParams() {
  return LOCALES.map(locale => ({
    locale,
  }));
}
