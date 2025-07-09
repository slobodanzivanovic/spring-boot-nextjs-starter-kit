import React from "react";
import styles from "./Spinner.module.css";

export type SpinnerSize = "small" | "medium" | "large";
export type SpinnerColor = "primary" | "white" | "dark";

type SpinnerProps = {
  size?: SpinnerSize;
  color?: SpinnerColor;
  fullPage?: boolean;
  className?: string;
  label?: string;
};

const Spinner: React.FC<SpinnerProps> = ({
  size = "medium",
  color = "primary",
  fullPage = false,
  className = "",
  label,
}) => {
  const containerClasses = [
    styles.container,
    fullPage ? styles.fullPage : "",
    className,
  ]
    .filter(Boolean)
    .join(" ");

  const spinnerClasses = [
    styles.spinner,
    styles[`size-${size}`],
    styles[`color-${color}`],
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <div className={containerClasses} role="status">
      <div className={spinnerClasses} />
      {label && <p className={styles.label}>{label}</p>}
      <span className={styles.srOnly}>Loading...</span>
    </div>
  );
};

export default Spinner;
