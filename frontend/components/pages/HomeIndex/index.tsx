"use client";

import React, {useRef, useState} from "react";
import {useTranslations} from "next-intl";
import Image from "next/image";
import {userApi} from "@/constants/api/api";
import {useAuth} from "@/lib/auth/AuthContext";
import {
  Avatar,
  Badge,
  Button,
  Card,
  Spinner,
  useToast,
} from "@/components/common";
import {ThemeToggle} from "@/components/ThemeToggle";
import {Link} from "@/i18n/navigation";

import styles from "./HomeIndex.module.css";

export default function HomeIndex() {
  const t = useTranslations("Home");
  const tUser = useTranslations("User");

  const {user, isAuthenticated, isLoading, logout} = useAuth();
  const [isUpdatingImage, setIsUpdatingImage] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const {showToast} = useToast();

  const handleProfilePictureClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith("image/")) {
      showToast(tUser("profileImageTypeError"), {
        type: "error",
      });
      return;
    }

    if (file.size > 2 * 1024 * 1024) {
      showToast(tUser("profileImageSizeError"), {
        type: "error",
      });
      return;
    }

    try {
      setIsUpdatingImage(true);

      await userApi.updateProfileImage(file);

      showToast(tUser("profileImageUpdated"), {
        type: "success",
      });

      window.location.reload();
    } catch (error) {
      console.error("Failed to update profile picture:", error);
      showToast(tUser("profileImageUpdateError"), {
        type: "error",
      });
    } finally {
      setIsUpdatingImage(false);
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
      showToast(tUser("logoutSuccess"), {
        type: "success",
      });
    } catch (error) {
      console.error("Error logging out:", error);
      showToast(tUser("logoutError"), {
        type: "error",
      });
    }
  };

  if (isLoading) {
    return <Spinner fullPage />;
  }

  return (
    <div className={styles.content}>
      {isAuthenticated && user ? (
        <Card className={styles.userSection}>
          <div className={styles.profileImageContainer}>
            <Avatar
              src={user.profileImageUrl || undefined}
              name={`${user.firstName} ${user.lastName}`}
              size="xlarge"
              onClick={handleProfilePictureClick}
              className={styles.profileImage}
            />
            <input
              type="file"
              ref={fileInputRef}
              style={{display: "none"}}
              accept="image/jpeg, image/png, image/gif, image/webp"
              onChange={handleFileChange}
            />
            {isUpdatingImage && (
              <Spinner size="small" className={styles.imageLoadingSpinner} />
            )}
          </div>

          <div className={styles.userInfo}>
            <h2>
              {user.firstName} {user.lastName}
            </h2>
            <p className={styles.username}>@{user.username}</p>
            <p className={styles.email}>{user.email}</p>
            <div className={styles.userRoles}>
              {user.roles.map((role, index) => (
                <Badge key={index} variant="primary" rounded>
                  {role.replace("ROLE_", "")}
                </Badge>
              ))}
            </div>
            <Button
              variant="danger"
              onClick={handleLogout}
              className={styles.logoutButton}
            >
              {tUser("logout")}
            </Button>
          </div>
        </Card>
      ) : (
        <div className={styles.welcomeSection}>
          <Image
            className={styles.logo}
            src="https://s3.tebi.io/programiraj/user%3A9890cc41-f6b3-46a5-bad3-ccbf088b224b/profile/bddb9fcb-6053-4962-b132-1857e29a0deb.png"
            alt={t("logoAlt")}
            width={350}
            height={450}
            priority
          />
          <h1 className={styles.title}>{t("title")}</h1>
          <div>
            <Link href="/login" className={styles.link}>
              Login
            </Link>
          </div>
        </div>
      )}

      <div className={styles.controls}>
        <ThemeToggle />
      </div>
      <div>
        <Link href="/components-preview" className={styles.link}>
          Components Preview
        </Link>
      </div>
    </div>
  );
}
