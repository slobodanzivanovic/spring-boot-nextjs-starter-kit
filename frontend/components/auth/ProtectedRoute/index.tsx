"use client";

import React, {useEffect, useState} from "react";
import {useAuth} from "@/lib/auth/AuthContext";
import {usePathname, useRouter} from "@/i18n/navigation";
import {useTranslations} from "next-intl";
import {Alert, Spinner, useToast} from "@/components/common";

import styles from "./ProtectedRoute.module.css";

type ProtectedRouteProps = {
  children: React.ReactNode;
  requireAuth?: boolean;
  redirectTo?: string;
  roles?: string[];
};

export default function ProtectedRoute({
  children,
  requireAuth = true,
  redirectTo = "/login",
  roles = [],
}: ProtectedRouteProps) {
  const t = useTranslations("AuthCommon");
  const {isAuthenticated, isLoading, user} = useAuth();
  const router = useRouter();
  const pathname = usePathname();
  const [hasRedirected, setHasRedirected] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const {showToast} = useToast();

  useEffect(() => {
    if (hasRedirected) return;
    if (isLoading) return;

    if (requireAuth && !isAuthenticated) {
      if (typeof window !== "undefined") {
        sessionStorage.setItem("redirectAfterLogin", pathname);
      }
      setHasRedirected(true);
      showToast(t("sessionExpired"), {
        type: "warning",
      });
      router.push(redirectTo);
      return;
    }

    if (requireAuth && isAuthenticated && roles.length > 0) {
      const userRoles = user?.roles || [];
      const hasRequiredRole = roles.some(role => userRoles.includes(role));

      if (!hasRequiredRole) {
        setErrorMessage(t("unauthorizedAccess"));
        setHasRedirected(true);
        showToast(t("unauthorizedAccess"), {
          type: "error",
          title: t("error"),
        });
        router.push("/unauthorized"); // TOOD: create thius
        return;
      }
    }

    if (!requireAuth && isAuthenticated) {
      const redirectPath =
        typeof window !== "undefined"
          ? sessionStorage.getItem("redirectAfterLogin")
          : null;

      if (redirectPath && typeof window !== "undefined") {
        sessionStorage.removeItem("redirectAfterLogin");
      }

      setHasRedirected(true);
      router.push(redirectPath || "/");
      return;
    }
  }, [
    isAuthenticated,
    isLoading,
    router,
    pathname,
    requireAuth,
    redirectTo,
    roles,
    user,
    hasRedirected,
    t,
    showToast,
  ]);

  if (
    isLoading ||
    (requireAuth && !isAuthenticated && !hasRedirected) ||
    (!requireAuth && isAuthenticated && !hasRedirected)
  ) {
    return <Spinner fullPage />;
  }

  if (errorMessage) {
    return (
      <div className={styles.errorContainer}>
        <Alert variant="error" title={t("error")}>
          {errorMessage}
        </Alert>
      </div>
    );
  }

  return <>{children}</>;
}
