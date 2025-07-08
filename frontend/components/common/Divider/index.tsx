import React from "react";
import styles from "./Divider.module.css";

type DividerProps = {
  text?: React.ReactNode;
  orientation?: "horizontal" | "vertical";
  className?: string;
  textClassName?: string;
};

const Divider: React.FC<DividerProps> = ({
  text,
  orientation = "horizontal",
  className = "",
  textClassName = "",
}) => {
  const dividerClasses = [styles.divider, styles[orientation], className]
    .filter(Boolean)
    .join(" ");

  const textClasses = [styles.text, textClassName].filter(Boolean).join(" ");

  if (text && orientation === "horizontal") {
    return (
      <div className={dividerClasses}>
        <span className={textClasses}>{text}</span>
      </div>
    );
  }

  return <div className={dividerClasses} />;
};

export default Divider;
