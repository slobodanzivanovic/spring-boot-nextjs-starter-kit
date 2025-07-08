import React from "react";
import styles from "./Container.module.css";

type ContainerSize = "small" | "medium" | "large" | "full";

type ContainerProps = {
  children: React.ReactNode;
  size?: ContainerSize;
  centered?: boolean;
  padded?: boolean;
  className?: string;
};

const Container: React.FC<ContainerProps> = ({
  children,
  size = "medium",
  centered = false,
  padded = true,
  className = "",
}) => {
  const containerClasses = [
    styles.container,
    styles[`size-${size}`],
    centered ? styles.centered : "",
    padded ? styles.padded : "",
    className,
  ]
    .filter(Boolean)
    .join(" ");

  return <div className={containerClasses}>{children}</div>;
};

export default Container;
