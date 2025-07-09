import styles from "./FormGroup.module.css";

type FormGroupProps = {
  children: React.ReactNode;
  className?: string;
  layout?: "vertical" | "horizontal";
  gap?: "small" | "medium" | "large";
};

const FormGroup: React.FC<FormGroupProps> = ({
  children,
  className = "",
  layout = "vertical",
  gap = "medium",
}) => {
  const formGroupClasses = [
    styles.formGroup,
    styles[`layout-${layout}`],
    styles[`gap-${gap}`],
    className,
  ]
    .filter(Boolean)
    .join(" ");

  return <div className={formGroupClasses}>{children}</div>;
};

export default FormGroup;
