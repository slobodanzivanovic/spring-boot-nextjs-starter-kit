import {Metadata} from "next";
import {getTranslations} from "next-intl/server";
import {constructMetadata} from "@/lib/metadata";
import {Locale} from "@/i18n/navigation";
import ProtectedRoute from "@/components/auth/ProtectedRoute";
import AdminDashboard from "@/components/admin/AdminDashboard";
import {LOCALES} from "@/i18n/routing";

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
  const t = await getTranslations({locale, namespace: "AdminDashboard"});

  return constructMetadata({
    page: "Admin",
    title: t("title"),
    description: t("description"),
    locale: locale as Locale,
    path: `/admin`,
  });
}

export default function Admin() {
  return (
    <ProtectedRoute requireAuth={true} roles={["ROLE_ADMIN"]}>
      <AdminDashboard />
    </ProtectedRoute>
  );
}

export async function generateStaticParams() {
  return LOCALES.map(locale => ({
    locale,
  }));
}
