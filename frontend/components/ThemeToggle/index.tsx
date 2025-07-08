"use client";

import {Moon, Sun} from "lucide-react";
import {useTheme} from "next-themes";
import {JSX, useEffect, useRef, useState} from "react";
import styles from "./ThemeToggle.module.css";
import {useTranslations} from "next-intl";

export function ThemeToggle(): JSX.Element | null {
  const t = useTranslations("CommonTheme");
  const {theme, setTheme} = useTheme();
  const [open, setOpen] = useState<boolean>(false);
  const [mounted, setMounted] = useState<boolean>(false);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent): void => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setOpen(false);
      }
    };

    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [open]);

  if (!mounted) {
    return null;
  }

  return (
    <div ref={menuRef} className={styles.themeToggle}>
      <button
        onClick={() => setOpen(!open)}
        className={styles.toggleButton}
        aria-label={t("toggleTheme")}
      >
        {theme === "dark" ? (
          <Moon className={styles.icon} />
        ) : (
          <Sun className={styles.icon} />
        )}
        <span className={styles.label}>{t("toggleTheme")}</span>
      </button>

      {open && (
        <div className={styles.dropdown}>
          <button
            className={theme === "light" ? styles.active : ""}
            onClick={() => {
              setTheme("light");
              setOpen(false);
            }}
          >
            <Sun className={styles.menuIcon} />
            {t("light")}
          </button>
          <button
            className={theme === "dark" ? styles.active : ""}
            onClick={() => {
              setTheme("dark");
              setOpen(false);
            }}
          >
            <Moon className={styles.menuIcon} />
            {t("dark")}
          </button>
          <button
            className={theme === "system" ? styles.active : ""}
            onClick={() => {
              setTheme("system");
              setOpen(false);
            }}
          >
            <svg
              className={styles.menuIcon}
              width="16"
              height="16"
              viewBox="0 0 16 16"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M8 3.5C5.51472 3.5 3.5 5.51472 3.5 8C3.5 10.4853 5.51472 12.5 8 12.5C10.4853 12.5 12.5 10.4853 12.5 8C12.5 5.51472 10.4853 3.5 8 3.5ZM2 8C2 4.68629 4.68629 2 8 2C11.3137 2 14 4.68629 14 8C14 11.3137 11.3137 14 8 14C4.68629 14 2 11.3137 2 8Z"
                fill="currentColor"
              />
            </svg>
            {t("system")}
          </button>
        </div>
      )}
    </div>
  );
}
