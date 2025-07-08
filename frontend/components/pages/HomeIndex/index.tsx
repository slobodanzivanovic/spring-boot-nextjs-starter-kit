"use client";

import {useTranslations} from "next-intl";
import styles from "./page.module.css";

export default function HomeIndex() {
  const t = useTranslations("Home");

  const handleGoogleLogin = () => {
    window.location.href =
      "http://localhost:8080/api/v1/auth/oauth2/authorization/google";
  };

  const handleGithubLogin = () => {
    window.location.href =
      "http://localhost:8080/api/v1/auth/oauth2/authorization/github";
  };

  return (
    <div className={styles.page}>

      <h1>{t("title")}</h1>

      <div>
        <h2>Login with</h2>
        <button onClick={handleGoogleLogin}>Google</button>
        <button onClick={handleGithubLogin}>GitHub</button>
      </div>
    </div>
  );
}
