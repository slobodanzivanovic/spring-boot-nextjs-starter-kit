"use client";

import React, {useEffect, useState} from "react";
import {useAuthFunctions} from "@/lib/auth/use-auth";
import {Link, useRouter} from "@/i18n/navigation";
import {useAuth} from "@/lib/auth/AuthContext";
import {useTranslations} from "next-intl";
import {Button, Card, Input, Spinner, useToast} from "@/components/common";

import styles from "./ResetPasswordForm.module.css";

enum ResetStep {
  EMAIL = "email",
  RESET = "reset",
  SUCCESS = "success",
}

const ResetPasswordForm = () => {
  const t = useTranslations("AuthResetPassword");
  const [currentStep, setCurrentStep] = useState<ResetStep>(ResetStep.EMAIL);
  const [email, setEmail] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [passwordsMatch, setPasswordsMatch] = useState(true);
  const [resendCooldown, setResendCooldown] = useState(0);

  const {requestPasswordReset, resetPassword, isLoading, error} =
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

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setNewPassword(value);
    setPasswordsMatch(value === confirmPassword);
  };

  const handleConfirmPasswordChange = (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const value = e.target.value;
    setConfirmPassword(value);
    setPasswordsMatch(newPassword === value);
  };

  const handleEmailSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const success = await requestPasswordReset(email);
    if (success) {
      setCurrentStep(ResetStep.RESET);
      setResendCooldown(60);
      showToast(t("resetCodeSent"), {
        type: "success",
      });
    } else if (error) {
      showToast(error, {
        type: "error",
      });
    }
  };

  const handleResetSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!passwordsMatch) {
      showToast(t("passwordsDoNotMatch"), {
        type: "error",
      });
      return;
    }

    const success = await resetPassword(email, verificationCode, newPassword);
    if (success) {
      setCurrentStep(ResetStep.SUCCESS);
      showToast(t("passwordUpdatedSuccess"), {
        type: "success",
      });
    } else if (error) {
      showToast(error, {
        type: "error",
      });
    }
  };

  const handleResendCode = async () => {
    if (resendCooldown > 0 || !email) return;

    const success = await requestPasswordReset(email);
    if (success) {
      setResendCooldown(60);
      showToast(t("resetCodeSent"), {
        type: "success",
      });
    } else if (error) {
      showToast(error, {
        type: "error",
      });
    }
  };

  const renderEmailStep = () => {
    return (
      <>
        <form onSubmit={handleEmailSubmit} className={styles.formWrapper}>
          <Input
            label={t("email")}
            type="email"
            id="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
            placeholder={t("emailPlaceholder")}
          />

          <Button
            variant="primary"
            type="submit"
            fullWidth
            isLoading={isLoading}
            className={styles.submitButton}
          >
            {isLoading ? t("sending") : t("sendResetCode")}
          </Button>
        </form>
      </>
    );
  };

  const renderResetStep = () => {
    return (
      <>
        <form onSubmit={handleResetSubmit} className={styles.formWrapper}>
          <Input
            label={t("verificationCode")}
            type="text"
            id="verificationCode"
            value={verificationCode}
            onChange={e => setVerificationCode(e.target.value)}
            required
            placeholder={t("verificationCodePlaceholder")}
          />

          <Input
            label={t("newPassword")}
            type="password"
            id="newPassword"
            value={newPassword}
            onChange={handlePasswordChange}
            required
            minLength={6}
            maxLength={40}
            placeholder={t("newPasswordPlaceholder")}
          />

          <Input
            label={t("confirmPassword")}
            type="password"
            id="confirmPassword"
            value={confirmPassword}
            onChange={handleConfirmPasswordChange}
            required
            placeholder={t("confirmPasswordPlaceholder")}
            errorMessage={
              !passwordsMatch && confirmPassword
                ? t("passwordsDoNotMatch")
                : undefined
            }
          />

          <Button
            variant="primary"
            type="submit"
            fullWidth
            isLoading={isLoading}
            disabled={isLoading || !passwordsMatch}
            className={styles.submitButton}
          >
            {isLoading ? t("updating") : t("updatePassword")}
          </Button>
        </form>

        <div className={styles.resendWrapper}>
          <p className={styles.resendText}>{t("didntReceiveCode")}</p>
          <Button
            variant="text"
            onClick={handleResendCode}
            disabled={resendCooldown > 0 || isLoading}
            className={styles.resendButton}
          >
            {resendCooldown > 0
              ? t("resendCodeWithTimer", {seconds: resendCooldown})
              : t("resendCode")}
          </Button>
        </div>

        <div className={styles.backLink}>
          <Button
            variant="text"
            onClick={() => setCurrentStep(ResetStep.EMAIL)}
            type="button"
            className={styles.textButton}
          >
            ← {t("useDifferentEmail")}
          </Button>
        </div>
      </>
    );
  };

  const renderSuccessStep = () => {
    return (
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
        <h2>{t("passwordUpdatedSuccess")}</h2>
        <p>{t("passwordUpdatedMessage")}</p>
        <Link href="/login" className={styles.linkButton}>
          {t("loginToAccount")}
        </Link>
      </div>
    );
  };

  const getCurrentStepContent = () => {
    switch (currentStep) {
      case ResetStep.EMAIL:
        return renderEmailStep();
      case ResetStep.RESET:
        return renderResetStep();
      case ResetStep.SUCCESS:
        return renderSuccessStep();
      default:
        return renderEmailStep();
    }
  };

  const getCardTitle = () => {
    if (currentStep === ResetStep.SUCCESS) return null;
    return t("title");
  };

  const getCardSubtitle = () => {
    if (currentStep === ResetStep.SUCCESS) return null;
    if (currentStep === ResetStep.EMAIL) return t("emailStepSubtitle");
    return t("resetStepSubtitle", {email});
  };

  return (
    <Card
      title={getCardTitle()}
      subtitle={getCardSubtitle()}
      className={styles.container}
      footerClassName={styles.footer}
      footer={
        currentStep !== ResetStep.SUCCESS ? (
          <Link href="/login" className={styles.link}>
            {t("backToLogin")}
          </Link>
        ) : null
      }
    >
      {getCurrentStepContent()}
    </Card>
  );
};

export default ResetPasswordForm;
