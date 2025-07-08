import React from "react";
import styles from "./Card.module.css";

type CardProps = {
  children: React.ReactNode;
  title?: string | null;
  subtitle?: string | null;
  footer?: React.ReactNode;
  className?: string;
  headerClassName?: string;
  bodyClassName?: string;
  footerClassName?: string;
  titleClassName?: string;
  subtitleClassName?: string;
};

const Card: React.FC<CardProps> = ({
  children,
  title,
  subtitle,
  footer,
  className = "",
  headerClassName = "",
  bodyClassName = "",
  footerClassName = "",
  titleClassName = "",
  subtitleClassName = "",
}) => {
  const cardClasses = [styles.card, className].filter(Boolean).join(" ");
  const headerClasses = [styles.header, headerClassName]
    .filter(Boolean)
    .join(" ");
  const bodyClasses = [styles.body, bodyClassName].filter(Boolean).join(" ");
  const footerClasses = [styles.footer, footerClassName]
    .filter(Boolean)
    .join(" ");
  const titleClasses = [styles.title, titleClassName].filter(Boolean).join(" ");
  const subtitleClasses = [styles.subtitle, subtitleClassName]
    .filter(Boolean)
    .join(" ");

  return (
    <div className={cardClasses}>
      {(title || subtitle) && (
        <div className={headerClasses}>
          {title && <h2 className={titleClasses}>{title}</h2>}
          {subtitle && <p className={subtitleClasses}>{subtitle}</p>}
        </div>
      )}
      <div className={bodyClasses}>{children}</div>
      {footer && <div className={footerClasses}>{footer}</div>}
    </div>
  );
};

export default Card;
