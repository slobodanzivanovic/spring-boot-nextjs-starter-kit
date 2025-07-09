import React, {forwardRef, InputHTMLAttributes} from "react";
import styles from "./Input.module.css";

export type InputVariant = "default" | "outlined" | "filled";

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  label?: string;
  variant?: InputVariant;
  errorMessage?: string;
  icon?: React.ReactNode;
  fullWidth?: boolean;
  wrapperClassName?: string;
  labelClassName?: string;
  inputClassName?: string;
  errorClassName?: string;
};

const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      label,
      variant = "default",
      errorMessage,
      icon,
      fullWidth = true,
      wrapperClassName = "",
      labelClassName = "",
      inputClassName = "",
      errorClassName = "",
      disabled,
      id,
      ...props
    },
    ref,
  ) => {
    const uniqueId = id || `input-${Math.random().toString(36).substr(2, 9)}`;

    const wrapperClasses = [
      styles.wrapper,
      fullWidth ? styles.fullWidth : "",
      wrapperClassName,
    ]
      .filter(Boolean)
      .join(" ");

    const labelClasses = [
      styles.label,
      disabled ? styles.labelDisabled : "",
      labelClassName,
    ]
      .filter(Boolean)
      .join(" ");

    const inputClasses = [
      styles.input,
      styles[`variant-${variant}`],
      errorMessage ? styles.hasError : "",
      icon ? styles.hasIcon : "",
      disabled ? styles.disabled : "",
      inputClassName,
    ]
      .filter(Boolean)
      .join(" ");

    const errorClasses = [styles.errorMessage, errorClassName]
      .filter(Boolean)
      .join(" ");

    return (
      <div className={wrapperClasses}>
        {label && (
          <label className={labelClasses} htmlFor={uniqueId}>
            {label}
          </label>
        )}
        <div className={styles.inputContainer}>
          {icon && <div className={styles.icon}>{icon}</div>}
          <input
            id={uniqueId}
            ref={ref}
            className={inputClasses}
            disabled={disabled}
            {...props}
          />
        </div>
        {errorMessage && <p className={errorClasses}>{errorMessage}</p>}
      </div>
    );
  },
);

Input.displayName = "Input";

export default Input;
