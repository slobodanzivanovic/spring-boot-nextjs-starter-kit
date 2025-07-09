"use client";

import React, {forwardRef, useEffect} from "react";
import {X} from "lucide-react";
import styles from "./Modal.module.css";

type ModalProps = {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
  closeOnOverlayClick?: boolean;
  size?: "small" | "medium" | "large" | "full";
  className?: string;
  contentClassName?: string;
  headerClassName?: string;
  bodyClassName?: string;
  footerClassName?: string;
};

const Modal = forwardRef<HTMLDivElement, ModalProps>(
  (
    {
      isOpen,
      onClose,
      title,
      children,
      footer,
      closeOnOverlayClick = true,
      size = "medium",
      className = "",
      contentClassName = "",
      headerClassName = "",
      bodyClassName = "",
      footerClassName = "",
    },
    ref,
  ) => {
    if (!isOpen) return null;

    const handleOverlayClick = (e: React.MouseEvent<HTMLDivElement>) => {
      if (e.target === e.currentTarget && closeOnOverlayClick) {
        onClose();
      }
    };

    const modalClasses = [styles.modal, className].filter(Boolean).join(" ");
    const contentClasses = [
      styles.content,
      styles[`size-${size}`],
      contentClassName,
    ]
      .filter(Boolean)
      .join(" ");
    const headerClasses = [styles.header, headerClassName]
      .filter(Boolean)
      .join(" ");
    const bodyClasses = [styles.body, bodyClassName].filter(Boolean).join(" ");
    const footerClasses = [styles.footer, footerClassName]
      .filter(Boolean)
      .join(" ");

    // TODO: FIX THIS
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useEffect(() => {
      if (isOpen) {
        document.body.style.overflow = "hidden";
      } else {
        document.body.style.overflow = "";
      }
      return () => {
        document.body.style.overflow = "";
      };
    }, [isOpen]);

    return (
      <div className={modalClasses} onClick={handleOverlayClick}>
        <div className={contentClasses} ref={ref}>
          {title && (
            <div className={headerClasses}>
              <h2 className={styles.title}>{title}</h2>
              <button
                className={styles.closeButton}
                onClick={onClose}
                aria-label="Close modal"
              >
                <X width="24" height="24" />
              </button>
            </div>
          )}
          <div className={bodyClasses}>{children}</div>
          {footer && <div className={footerClasses}>{footer}</div>}
        </div>
      </div>
    );
  },
);

Modal.displayName = "Modal";

export default Modal;
