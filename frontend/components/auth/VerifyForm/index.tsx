"use client";

import React, {useEffect, useState} from "react";
import {VerifyRequest} from "@/types/auth";
import {useAuthFunctions} from "@/lib/auth/use-auth";
import {Link, useRouter} from "@/i18n/navigation";
import {useAuth} from "@/lib/auth/AuthContext";
import {useSearchParams} from "next/navigation";
import {useTranslations} from "next-intl";
import {
  Alert,
  Button,
  Card,
  Input,
  Spinner,
  useToast,
} from "@/components/common";

import styles from "./VerifyForm.module.css";

const VerifyForm = () => {
  const t = useTranslations("AuthVerify");
  const searchParams = useSearchParams();
  const initialEmail = searchParams?.get("email") || "";
  const fromRegistration =
    searchParams?.get("fromRegistration") === "true" || false;

  const [formData, setFormData] = useState<VerifyRequest>({
    email: initialEmail,
    verificationCode: "",
  });
  const [verificationSuccess, setVerificationSuccess] = useState(false);
  const [resendSuccess, setResendSuccess] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);

  const {verifyAccount, resendVerification, isLoading, error} =
    useAuthFunctions();
  const router = useRouter();
  const {isAuthenticated} = useAuth();
  const {showToast} = useToast();

  useEffect(() => {
    if (isAuthenticated) {
      router.push("/");
    }
  }, [isAuthenticated, router]);

  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (resendCooldown > 0) {
      timer = setTimeout(() => {
        setResendCooldown(prev => prev - 1);
      }, 1000);
    }
    return () => {
      if (timer) clearTimeout(timer);
    };
  }, [resendCooldown]);

  if (isAuthenticated) {
    return <Spinner fullPage />;
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const {name, value} = e.target;
    setFormData(prev => ({...prev, [name]: value}));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const success = await verifyAccount(formData);
    if (success) {
      setVerificationSuccess(true);
      showToast(t("verificationSuccess"), {
        type: "success",
      });
    } else if (error) {
      showToast(error, {
        type: "error",
        title: t("verificationFailed"),
      });
    }
  };

  const handleResendCode = async () => {
    if (resendCooldown > 0 || !formData.email) return;

    const success = await resendVerification(formData.email);
    if (success) {
      setResendSuccess(true);
      setResendCooldown(60);
      showToast(t("verificationCodeResent"), {
        type: "success",
      });

      setTimeout(() => setResendSuccess(false), 5000);
    } else if (error) {
      showToast(error, {
        type: "error",
      });
    }
  };

  if (verificationSuccess) {
    return (
      <Card className={styles.container}>
        <div className={styles.successMessage}>
          <div className={styles.successIcon}>
            <svg
              width="48"
              height="48"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M12 22C6.477 22 2 17.523 2 12S6.477 2 12 2s10 4.477 10 10-4.477 10-10 10zm-.997-6l7.07-7.071-1.414-1.414-5.656 5.657-2.829-2.829-1.414 1.414L11.003 16z"
                fill="currentColor"
              />
            </svg>
          </div>
          <h2>{t("verificationSuccess")}</h2>
          <p>{t("verificationSuccessMessage")}</p>
          <Link href="/login" className={styles.linkButton}>
            {t("loginToAccount")}
          </Link>
        </div>
      </Card>
    );
  }

  return (
    <Card
      title={t("title")}
      subtitle={t("subtitle")}
      className={styles.container}
      footerClassName={styles.footer}
      footer={
        <Link href="/login" className={styles.link}>
          {t("backToLogin")}
        </Link>
      }
    >
      {fromRegistration && (
        <Alert variant="info" className={styles.welcomeMessage}>
          <p>
            <strong>{t("registrationSuccessful")}</strong>{" "}
            {t("completeSetupMessage")}
          </p>
        </Alert>
      )}

      <div className={styles.formWrapper}>
        <form onSubmit={handleSubmit}>
          <Input
            label={t("email")}
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            placeholder={t("emailPlaceholder")}
          />

          <Input
            label={t("verificationCode")}
            type="text"
            id="verificationCode"
            name="verificationCode"
            value={formData.verificationCode}
            onChange={handleChange}
            required
            placeholder={t("verificationCodePlaceholder")}
          />

          {resendSuccess && (
            <Alert variant="success" className={styles.successAlert}>
              {t("verificationCodeResent")}
            </Alert>
          )}

          <Button
            variant="primary"
            type="submit"
            fullWidth
            isLoading={isLoading}
            className={styles.submitButton}
          >
            {isLoading ? t("verifying") : t("verifyAccount")}
          </Button>
        </form>

        <div className={styles.resendWrapper}>
          <p className={styles.resendText}>{t("didntReceiveCode")}</p>
          <Button
            variant="text"
            onClick={handleResendCode}
            disabled={resendCooldown > 0 || !formData.email || isLoading}
            className={styles.resendButton}
          >
            {resendCooldown > 0
              ? `${t("resendVerificationCode")} (${resendCooldown}s)`
              : t("resendVerificationCode")}
          </Button>
        </div>
      </div>
    </Card>
  );
};

export default VerifyForm;
