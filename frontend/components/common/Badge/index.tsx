import React from "react";
import styles from "./Badge.module.css";

export type BadgeVariant =
  | "primary"
  | "secondary"
  | "success"
  | "error"
  | "warning"
  | "info";
export type BadgeSize = "small" | "medium" | "large";

type BadgeProps = {
  children: React.ReactNode;
  variant?: BadgeVariant;
  size?: BadgeSize;
  rounded?: boolean;
  className?: string;
};

const Badge: React.FC<BadgeProps> = ({
  children,
  variant = "primary",
  size = "medium",
  rounded = false,
  className = "",
}) => {
  const badgeClasses = [
    styles.badge,
    styles[`variant-${variant}`],
    styles[`size-${size}`],
    rounded ? styles.rounded : "",
    className,
  ]
    .filter(Boolean)
    .join(" ");

  return <span className={badgeClasses}>{children}</span>;
};

export default Badge;
