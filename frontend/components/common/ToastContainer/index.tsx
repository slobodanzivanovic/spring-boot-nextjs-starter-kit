"use client";

import React, {useCallback, useEffect, useState} from "react";
import {createPortal} from "react-dom";
import Toast, {ToastPosition, ToastType} from "@/components/common/Toast";
import styles from "./ToastContainer.module.css";

type ToastOptions = {
  id?: string;
  type?: ToastType;
  title?: string;
  duration?: number;
  position?: ToastPosition;
};

type ToastItem = {
  id: string;
  message: string;
  type: ToastType;
  title?: string;
  duration?: number;
  position: ToastPosition;
};

interface ToastContextType {
  showToast: (message: string, options?: ToastOptions) => string;
  dismissToast: (id: string) => void;
  dismissAllToasts: () => void;
}

export const ToastContext = React.createContext<ToastContextType | undefined>(
  undefined,
);

export const useToast = () => {
  const context = React.useContext(ToastContext);
  if (context === undefined) {
    throw new Error("useToast must be used within a ToastProvider");
  }
  return context;
};

interface ToastProviderProps {
  children: React.ReactNode;
  defaultPosition?: ToastPosition;
  defaultDuration?: number;
}

export const ToastProvider: React.FC<ToastProviderProps> = ({
  children,
  defaultPosition = "top-right",
  defaultDuration = 5000,
}) => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);
  const [portalContainer, setPortalContainer] = useState<HTMLElement | null>(
    null,
  );

  useEffect(() => {
    const container = document.createElement("div");
    container.className = styles.toastPortalContainer;
    document.body.appendChild(container);
    setPortalContainer(container);

    return () => {
      document.body.removeChild(container);
    };
  }, []);

  const showToast = useCallback(
    (message: string, options: ToastOptions = {}): string => {
      const id = options.id || Date.now().toString();
      const newToast: ToastItem = {
        id,
        message,
        type: options.type || "info",
        title: options.title,
        duration: options.duration || defaultDuration,
        position: options.position || defaultPosition,
      };

      setToasts(prevToasts => [...prevToasts, newToast]);
      return id;
    },
    [defaultDuration, defaultPosition],
  );

  const dismissToast = useCallback((id: string) => {
    setToasts(prevToasts => prevToasts.filter(toast => toast.id !== id));
  }, []);

  const dismissAllToasts = useCallback(() => {
    setToasts([]);
  }, []);

  const groupedToasts = toasts.reduce<Record<ToastPosition, ToastItem[]>>(
    (acc, toast) => {
      if (!acc[toast.position]) {
        acc[toast.position] = [];
      }
      acc[toast.position].push(toast);
      return acc;
    },
    {
      "top-right": [],
      "top-left": [],
      "bottom-right": [],
      "bottom-left": [],
      "top-center": [],
      "bottom-center": [],
    },
  );

  const renderToastsByPosition = (position: ToastPosition) => {
    const toastsInPosition = groupedToasts[position];
    if (toastsInPosition.length === 0) return null;

    return (
      <div
        key={position}
        className={`${styles.toastContainer} ${styles[`position-${position}`]}`}
      >
        {toastsInPosition.map(toast => (
          <Toast
            key={toast.id}
            id={toast.id}
            type={toast.type}
            message={toast.message}
            title={toast.title}
            duration={toast.duration}
            position={toast.position}
            onClose={dismissToast}
          />
        ))}
      </div>
    );
  };

  return (
    <ToastContext.Provider value={{showToast, dismissToast, dismissAllToasts}}>
      {children}
      {portalContainer &&
        createPortal(
          <>
            {renderToastsByPosition("top-left")}
            {renderToastsByPosition("top-center")}
            {renderToastsByPosition("top-right")}
            {renderToastsByPosition("bottom-left")}
            {renderToastsByPosition("bottom-center")}
            {renderToastsByPosition("bottom-right")}
          </>,
          portalContainer,
        )}
    </ToastContext.Provider>
  );
};
