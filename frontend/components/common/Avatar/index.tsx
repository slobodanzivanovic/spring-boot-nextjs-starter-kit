import React from "react";
import styles from "./Avatar.module.css";
import Image from "next/image";

export type AvatarSize = "small" | "medium" | "large" | "xlarge";

type AvatarProps = {
  src?: string;
  alt?: string;
  name?: string;
  size?: AvatarSize;
  className?: string;
  onClick?: () => void;
};

const getInitials = (name: string): string => {
  const names = name.split(" ");
  // TODO: fix this...
  if (names.length === 1) {
    return names[0].charAt(0).toUpperCase();
  }
  return (names[0].charAt(0) + names[names.length - 1].charAt(0)).toUpperCase();
};

const Avatar: React.FC<AvatarProps> = ({
  src,
  alt = "Avatar",
  name = "",
  size = "medium",
  className = "",
  onClick,
}) => {
  const [imageError, setImageError] = React.useState(false);

  const handleError = () => {
    setImageError(true);
  };

  const avatarClasses = [
    styles.avatar,
    styles[`size-${size}`],
    onClick ? styles.clickable : "",
    className,
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <div className={avatarClasses} onClick={onClick}>
      {src && !imageError ? (
        <Image
          src={src}
          alt={alt}
          width={200}
          height={200}
          className={styles.image}
          onError={handleError}
        />
      ) : name ? (
        <div className={styles.placeholder}>{getInitials(name)}</div>
      ) : (
        <div className={styles.placeholder}>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
        </div>
      )}
    </div>
  );
};

export default Avatar;
