"use client";

import {LOCALE_NAMES, routing} from "@/i18n/routing";
import {useLocale, useTranslations} from "next-intl";
import {useParams} from "next/navigation";
import {useEffect, useRef, useState, useTransition} from "react";
import httpClient from "@/lib/httpClient";
import {Locale, usePathname, useRouter} from "@/i18n/navigation";
import {getLocale, setLocale} from "@/lib/locale";
import {Languages} from "lucide-react";
import styles from "./LocaleSwitcher.module.css";

export default function LocaleSwitcher() {
  const t = useTranslations("CommonLanguage");
  const router = useRouter();
  const pathname = usePathname();
  const params = useParams();
  const locale = useLocale();
  const [isPending, startTransition] = useTransition();
  const [currentLocale, setCurrentLocale] = useState("locale");
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setCurrentLocale(locale);
  }, [locale, setCurrentLocale]);

  useEffect(() => {
    httpClient.defaults.headers.common["Accept-Language"] = getLocale();
  }, [currentLocale]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen]);

  function onLocaleSelect(nextLocale: Locale) {
    if (nextLocale === currentLocale) {
      setIsOpen(false);
      return;
    }

    setCurrentLocale(nextLocale);
    setLocale(nextLocale);
    httpClient.defaults.headers.common["Accept-Language"] = nextLocale;
    setIsOpen(false);

    startTransition(() => {
      router.replace(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-expect-error
        {pathname, params: params || {}},
        {locale: nextLocale},
      );
    });
  }

  const localeCount = routing.locales.length;
  const columns = Math.ceil(Math.sqrt(localeCount));
  const rows = Math.ceil(localeCount / columns);

  return (
    <div className={styles.container} ref={dropdownRef}>
      <button
        type="button"
        className={`${styles.trigger} ${isOpen ? styles.triggerActive : ""}`}
        onClick={() => setIsOpen(!isOpen)}
        disabled={isPending}
        aria-label={t("switchLanguage")}
        aria-expanded={isOpen}
      >
        <Languages className={styles.triggerIcon} />
      </button>

      {isOpen && (
        <div className={styles.dropdown}>
          <div className={styles.contentWrapper}>
            <div
              className={styles.languageGrid}
              style={
                {
                  "--columns": columns,
                  "--rows": rows,
                } as React.CSSProperties
              }
            >
              {routing.locales.map((localeCode, index) => {
                const row = Math.floor(index / columns) + 1;
                const column = (index % columns) + 1;
                const isSelected = localeCode === currentLocale;

                return (
                  <button
                    key={localeCode}
                    type="button"
                    className={`${styles.languageItem} ${isSelected ? styles.languageItemSelected : ""}`}
                    onClick={() => onLocaleSelect(localeCode)}
                    style={
                      {
                        "--row": row,
                        "--column": column,
                      } as React.CSSProperties
                    }
                  >
                    <span className={styles.languageItemContent}>
                      {LOCALE_NAMES[localeCode]}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/*{isPending && (*/}
      {/*  <span className={styles.loadingIndicator}>{t("switching")}</span>*/}
      {/*)}*/}
    </div>
  );
}
