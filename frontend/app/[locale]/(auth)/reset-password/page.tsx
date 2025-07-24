import {Metadata} from "next";
import {getTranslations} from "next-intl/server";
import {constructMetadata} from "@/lib/metadata";
import {Locale} from "@/i18n/navigation";
import ResetPasswordForm from "@/components/auth/ResetPasswordForm";
import {LOCALES} from "@/i18n/routing";
import styles from "./ResetPassword.module.css";
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
  const t = await getTranslations({locale, namespace: "ResetPassword"});

  return constructMetadata({
    page: "ResetPassword",
    title: t("title"),
    description: t("description"),
    locale: locale as Locale,
    path: `/reset-password`,
  });
}

export default async function ResetPassword() {
  return (
    <ProtectedRoute requireAuth={false} redirectTo="/">
      <div className={styles.container}>
        <ResetPasswordForm />
      </div>
    </ProtectedRoute>
  );
}

export async function generateStaticParams() {
  return LOCALES.map(locale => ({
    locale,
  }));
}
