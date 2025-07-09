"use client";

import React, {useCallback, useEffect, useRef, useState} from "react";
import styles from "./Toast.module.css";
import {AlertTriangle, CheckCircle, Info, X, XCircle} from "lucide-react";

export type ToastType = "success" | "error" | "warning" | "info";
export type ToastPosition =
  | "top-right"
  | "top-left"
  | "bottom-right"
  | "bottom-left"
  | "top-center"
  | "bottom-center";

export interface ToastProps {
  id: string;
  type: ToastType;
  message: string;
  title?: string;
  duration?: number;
  position?: ToastPosition;
  onClose: (id: string) => void;
}

const Toast: React.FC<ToastProps> = ({
  id,
  type,
  message,
  title,
  duration = 5000,
  position = "top-right",
  onClose,
}) => {
  const [isVisible, setIsVisible] = useState(true);
  const [isPaused, setIsPaused] = useState(false);
  const [remainingTime, setRemainingTime] = useState(duration);

  const timerRef = useRef<NodeJS.Timeout | null>(null);
  const startTimeRef = useRef<number | null>(null);
  const closeTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  const handleClose = useCallback(() => {
    if (!isVisible) return;

    setIsVisible(false);

    if (timerRef.current) {
      clearTimeout(timerRef.current);
      timerRef.current = null;
    }

    closeTimeoutRef.current = setTimeout(() => {
      onClose(id);
    }, 300);
  }, [id, onClose, isVisible]);

  useEffect(() => {
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
      if (closeTimeoutRef.current) clearTimeout(closeTimeoutRef.current);
    };
  }, []);

  const handleMouseEnter = useCallback(() => {
    setIsPaused(true);

    if (timerRef.current && startTimeRef.current) {
      clearTimeout(timerRef.current);
      timerRef.current = null;

      const elapsedTime = Date.now() - startTimeRef.current;
      setRemainingTime(prev => Math.max(0, prev - elapsedTime));
      startTimeRef.current = null;
    }
  }, []);

  const handleMouseLeave = useCallback(() => {
    setIsPaused(false);

    if (
      !timerRef.current &&
      remainingTime > 0 &&
      isVisible &&
      duration !== Infinity
    ) {
      startTimeRef.current = Date.now();
      timerRef.current = setTimeout(handleClose, remainingTime);
    }
  }, [remainingTime, isVisible, duration, handleClose]);

  useEffect(() => {
    if (!isPaused && isVisible && duration !== Infinity) {
      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }

      startTimeRef.current = Date.now();
      timerRef.current = setTimeout(handleClose, remainingTime);
    }

    return () => {
      if (timerRef.current) {
        clearTimeout(timerRef.current);
        timerRef.current = null;
      }
    };
  }, [isPaused, isVisible, duration, remainingTime, handleClose]);

  const getIcon = () => {
    switch (type) {
      case "success":
        return <CheckCircle size={20} />;
      case "error":
        return <XCircle size={20} />;
      case "warning":
        return <AlertTriangle size={20} />;
      case "info":
        return <Info size={20} />;
      default:
        return <Info size={20} />;
    }
  };

  const getAnimationClass = () => {
    if (position.includes("right")) {
      return isVisible ? styles.slideInRight : styles.slideOutRight;
    } else if (position.includes("left")) {
      return isVisible ? styles.slideInLeft : styles.slideOutLeft;
    } else if (position.includes("top")) {
      return isVisible ? styles.slideInTop : styles.slideOutTop;
    } else if (position.includes("bottom")) {
      return isVisible ? styles.slideInBottom : styles.slideOutBottom;
    }
    return isVisible ? styles.slideInRight : styles.slideOutRight;
  };

  const toastClasses = [
    styles.toast,
    styles[`type-${type}`],
    getAnimationClass(),
  ]
    .filter(Boolean)
    .join(" ");

  const progressStyle = {
    animationDuration: `${duration}ms`,
    animationPlayState: isPaused ? "paused" : "running",
    animationFillMode: "forwards",
  };

  return (
    <div
      className={toastClasses}
      role="alert"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
    >
      <div className={styles.content}>
        <div className={styles.iconContainer}>{getIcon()}</div>
        <div className={styles.textContainer}>
          {title && <div className={styles.title}>{title}</div>}
          <div className={styles.message}>{message}</div>
        </div>
        <button
          className={styles.closeButton}
          onClick={handleClose}
          aria-label="Close"
        >
          <X size={18} />
        </button>
      </div>
      {duration !== Infinity && (
        <div className={styles.progressContainer}>
          <div className={styles.progressBar} style={progressStyle} />
        </div>
      )}
    </div>
  );
};

export default Toast;
