"use client";

import React, {useEffect, useState} from "react";
import {RegisterRequest} from "@/types/auth";
import {useAuthFunctions} from "@/lib/auth/use-auth";
import {Link, useRouter} from "@/i18n/navigation";
import {useAuth} from "@/lib/auth/AuthContext";
import {API_ENDPOINTS} from "@/constants/api/apiConfig";
import {useTranslations} from "next-intl";
import {
  Button,
  Card,
  Divider,
  FormGroup,
  Input,
  Spinner,
  useToast,
} from "@/components/common";

import styles from "./RegisterForm.module.css";

const RegisterForm = () => {
  const t = useTranslations("AuthRegister");
  const [formData, setFormData] = useState<RegisterRequest>({
    username: "",
    email: "",
    password: "",
    firstName: "",
    lastName: "",
  });
  const [confirmPassword, setConfirmPassword] = useState("");
  const [passwordsMatch, setPasswordsMatch] = useState(true);
  const {register, isLoading, error} = useAuthFunctions();
  const router = useRouter();
  const {isAuthenticated} = useAuth();
  const {showToast} = useToast();

  useEffect(() => {
    if (isAuthenticated) {
      router.push("/");
    }
  }, [isAuthenticated, router]);

  if (isAuthenticated) {
    return <Spinner fullPage />;
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const {name, value} = e.target;
    setFormData(prev => ({...prev, [name]: value}));

    if (name === "password") {
      setPasswordsMatch(confirmPassword === value);
    }
  };

  const handleConfirmPasswordChange = (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const {value} = e.target;
    setConfirmPassword(value);
    setPasswordsMatch(formData.password === value);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!passwordsMatch) {
      showToast(t("passwordsDoNotMatch"), {
        type: "error",
      });
      return;
    }

    const success = await register(formData);
    if (success) {
      showToast(t("registrationSuccess"), {
        type: "success",
      });
      router.push(
        `/verify?email=${encodeURIComponent(formData.email)}&fromRegistration=true`,
      );
    } else if (error) {
      showToast(error, {
        type: "error",
        title: t("registrationFailed"),
      });
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = API_ENDPOINTS.AUTH.OAUTH_GOOGLE;
  };

  const handleGithubLogin = () => {
    window.location.href = API_ENDPOINTS.AUTH.OAUTH_GITHUB;
  };

  return (
    <Card
      title={t("title")}
      subtitle={t("subtitle")}
      className={styles.container}
      footerClassName={styles.footer}
      footer={
        <div className={styles.loginAccount}>
          {t("alreadyHaveAccount")}{" "}
          <Link href="/login" className={styles.link}>
            {t("login")}
          </Link>
        </div>
      }
    >
      <div className={styles.formContainer}>
        <div className={styles.oauthButtons}>
          <Button
            variant="oauth"
            onClick={handleGoogleLogin}
            icon={
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M22.56 12.25C22.56 11.47 22.49 10.72 22.36 10H12V14.25H17.92C17.66 15.63 16.89 16.8 15.77 17.58V20.25H19.28C21.36 18.31 22.56 15.54 22.56 12.25Z"
                  fill="#4285F4"
                />
                <path
                  d="M12 23C14.97 23 17.46 22.07 19.28 20.25L15.77 17.58C14.77 18.25 13.48 18.67 12 18.67C9.07 18.67 6.64 16.73 5.76 14.08H2.11V16.83C3.92 20.53 7.67 23 12 23Z"
                  fill="#34A853"
                />
                <path
                  d="M5.76 14.08C5.53 13.42 5.4 12.71 5.4 12C5.4 11.29 5.53 10.58 5.76 9.92V7.17H2.11C1.4 8.65 1 10.29 1 12C1 13.71 1.4 15.35 2.11 16.83L5.76 14.08Z"
                  fill="#FBBC05"
                />
                <path
                  d="M12 5.33C13.62 5.33 15.07 5.9 16.21 7L19.35 3.87C17.46 2.09 14.97 1 12 1C7.67 1 3.92 3.47 2.11 7.17L5.76 9.92C6.64 7.27 9.07 5.33 12 5.33Z"
                  fill="#EA4335"
                />
              </svg>
            }
          >
            {t("continueWithGoogle")}
          </Button>

          <Button
            variant="oauth"
            onClick={handleGithubLogin}
            icon={
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  fillRule="evenodd"
                  clipRule="evenodd"
                  d="M12 0C5.37 0 0 5.37 0 12C0 17.31 3.435 21.795 8.205 23.385C8.805 23.49 9.03 23.13 9.03 22.815C9.03 22.53 9.015 21.585 9.015 20.58C6 21.135 5.22 19.845 4.98 19.17C4.845 18.825 4.26 17.76 3.75 17.475C3.33 17.25 2.73 16.695 3.735 16.68C4.68 16.665 5.355 17.55 5.58 17.91C6.66 19.725 8.385 19.215 9.075 18.9C9.18 18.12 9.495 17.595 9.84 17.295C7.17 16.995 4.38 15.96 4.38 11.37C4.38 10.065 4.845 8.985 5.61 8.145C5.49 7.845 5.07 6.615 5.73 4.965C5.73 4.965 6.735 4.65 9.03 6.195C9.99 5.925 11.01 5.79 12.03 5.79C13.05 5.79 14.07 5.925 15.03 6.195C17.325 4.635 18.33 4.965 18.33 4.965C18.99 6.615 18.57 7.845 18.45 8.145C19.215 8.985 19.68 10.05 19.68 11.37C19.68 15.975 16.875 16.995 14.205 17.295C14.64 17.67 15.015 18.39 15.015 19.515C15.015 21.12 15 22.41 15 22.815C15 23.13 15.225 23.505 15.825 23.385C18.2072 22.5807 20.2772 21.0497 21.7437 19.0074C23.2101 16.965 23.9993 14.5143 24 12C24 5.37 18.63 0 12 0Z"
                  fill="currentColor"
                />
              </svg>
            }
          >
            {t("continueWithGithub")}
          </Button>
        </div>

        <Divider text={t("or")} />

        <div className={styles.formWrapper}>
          <form onSubmit={handleSubmit}>
            <Input
              label={t("username") + "*"}
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              minLength={3}
              maxLength={50}
              placeholder={t("usernamePlaceholder")}
            />

            <Input
              label={t("email") + "*"}
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder={t("emailPlaceholder")}
            />

            <Input
              label={t("password") + "*"}
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              minLength={6}
              maxLength={40}
              placeholder={t("passwordPlaceholder")}
            />

            <Input
              label={t("confirmPassword") + "*"}
              type="password"
              id="confirmPassword"
              name="confirmPassword"
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

            <FormGroup layout="horizontal" gap="medium">
              <Input
                label={t("firstName")}
                type="text"
                id="firstName"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                maxLength={50}
                placeholder={t("firstNamePlaceholder")}
              />

              <Input
                label={t("lastName")}
                type="text"
                id="lastName"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                maxLength={50}
                placeholder={t("lastNamePlaceholder")}
              />
            </FormGroup>

            <div className={styles.terms}>
              {t("termsAgreement")}{" "}
              <Link href="/terms" className={styles.link}>
                {t("termsOfService")}
              </Link>{" "}
              {t("and")}{" "}
              <Link href="/privacy" className={styles.link}>
                {t("privacyPolicy")}
              </Link>
            </div>

            <Button
              variant="primary"
              type="submit"
              fullWidth
              isLoading={isLoading}
              disabled={isLoading || !passwordsMatch}
            >
              {isLoading ? t("creatingAccount") : t("createAccount")}
            </Button>
          </form>
        </div>
      </div>
    </Card>
  );
};

export default RegisterForm;
