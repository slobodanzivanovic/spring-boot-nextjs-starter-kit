"use client";

import React, {useEffect, useState} from "react";
import {useTranslations} from "next-intl";
import {UserResponse} from "@/types/auth";
import {adminApi} from "@/constants/api/api";
import {
  Alert,
  Avatar,
  Badge,
  Button,
  Card,
  Spinner,
  useToast,
} from "@/components/common";

import styles from "./AdminDashboard.module.css";

const AdminDashboard = () => {
  const t = useTranslations("AdminDashboard");
  const tCommon = useTranslations("Common");

  const [users, setUsers] = useState<UserResponse[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const {showToast} = useToast();

  const fetchUsers = async (page = 0) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await adminApi.getUsers(page);

      if (response.data.isSuccess) {
        const data = response.data.response;
        setUsers(data.data);
        setTotalPages(data.totalPages);
        setCurrentPage(data.page);
      } else {
        setError(t("errorFetchUsers"));
        showToast(t("errorFetchUsers"), {
          type: "error",
        });
      }
    } catch (err: any) {
      console.error("Error fetching users:", err);
      const errorMessage =
        err.response?.data?.message || t("errorGenericFetch");
      setError(errorMessage);
      showToast(errorMessage, {
        type: "error",
        title: tCommon("error"),
      });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const handlePageChange = (page: number) => {
    if (page >= 0 && page < totalPages) {
      fetchUsers(page);
    }
  };

  return (
    <div className={styles.container}>
      <Card title={t("title")} className={styles.mainCard}>
        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>{t("userManagementTitle")}</h2>

          {error && <Alert variant="error">{error}</Alert>}

          {isLoading ? (
            <div className={styles.loadingContainer}>
              <Spinner size="large" />
            </div>
          ) : (
            <>
              <div className={styles.tableContainer}>
                <table className={styles.table}>
                  <thead>
                    <tr>
                      <th>{t("tableHeaderId")}</th>
                      <th>{t("tableHeaderUsername")}</th>
                      <th>{t("tableHeaderEmail")}</th>
                      <th>{t("tableHeaderName")}</th>
                      <th>{t("tableHeaderRoles")}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.length > 0 ? (
                      users.map(user => (
                        <tr key={user.id}>
                          <td>{user.id}</td>
                          <td className={styles.usernameCell}>
                            <div className={styles.userInfo}>
                              <Avatar
                                src={user.profileImageUrl || undefined}
                                name={`${user.firstName} ${user.lastName}`}
                                size="small"
                                className={styles.userAvatar}
                              />
                              <span>{user.username}</span>
                            </div>
                          </td>
                          <td>{user.email}</td>
                          <td>
                            {user.firstName} {user.lastName}
                          </td>
                          <td>
                            <div className={styles.rolesContainer}>
                              {user.roles.map(role => (
                                <Badge
                                  key={role}
                                  variant={
                                    role === "ROLE_ADMIN"
                                      ? "primary"
                                      : "secondary"
                                  }
                                  rounded
                                  size="small"
                                >
                                  {role.replace("ROLE_", "")}
                                </Badge>
                              ))}
                            </div>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr className={styles.noUsersRow}>
                        <td colSpan={5}>{t("noUsersFound")}</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>

              {totalPages > 1 && (
                <div className={styles.pagination}>
                  <Button
                    variant="secondary"
                    size="small"
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 0}
                  >
                    {tCommon("previous")}
                  </Button>

                  <span className={styles.paginationInfo}>
                    {t("paginationPageInfo", {
                      current: currentPage + 1,
                      total: totalPages,
                    })}
                  </span>

                  <Button
                    variant="secondary"
                    size="small"
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages - 1}
                  >
                    {tCommon("next")}
                  </Button>
                </div>
              )}
            </>
          )}
        </section>
      </Card>
    </div>
  );
};

export default AdminDashboard;
