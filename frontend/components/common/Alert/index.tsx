import React from "react";
import styles from "./Alert.module.css";
import {AlertTriangle, CheckCircle, Info, XCircle} from "lucide-react";

export type AlertVariant = "success" | "error" | "warning" | "info";

type AlertProps = {
  children: React.ReactNode;
  variant?: AlertVariant;
  title?: string;
  icon?: React.ReactNode;
  onClose?: () => void;
  className?: string;
};

const variantIcons = {
  success: <CheckCircle size={20} />,
  error: <XCircle size={20} />,
  warning: <AlertTriangle size={20} />,
  info: <Info size={20} />,
};

const Alert: React.FC<AlertProps> = ({
  children,
  variant = "info",
  title,
  icon,
  onClose,
  className = "",
}) => {
  const alertClasses = [styles.alert, styles[`variant-${variant}`], className]
    .filter(Boolean)
    .join(" ");

  return (
    <div className={alertClasses} role="alert">
      <div className={styles.icon}>{icon || variantIcons[variant]}</div>
      <div className={styles.content}>
        {title && <div className={styles.title}>{title}</div>}
        <div className={styles.message}>{children}</div>
      </div>
      {onClose && (
        <button
          className={styles.closeButton}
          onClick={onClose}
          aria-label="Close alert"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="16"
            height="16"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      )}
    </div>
  );
};

export default Alert;
