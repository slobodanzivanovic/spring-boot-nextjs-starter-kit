"use client";

import {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useEffect,
  useState,
} from "react";
import {UserResponse} from "@/types/auth";
import {authApi, userApi} from "@/constants/api/api";
import {
  getToken,
  isTokenExpired,
  removeToken,
  setToken,
} from "@/lib/auth/token";

type AuthContextType = {
  user: UserResponse | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (token: string, expiresIn: number) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<UserResponse | null>;
  error: Error | null;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({children}: {children: ReactNode}) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  const isAuthenticated = !!user;

  const fetchCurrentUser =
    useCallback(async (): Promise<UserResponse | null> => {
      try {
        const token = getToken();

        if (!token || isTokenExpired()) {
          return null;
        }

        const response = await userApi.getCurrentUser();
        if (response.data.isSuccess) {
          return response.data.response;
        }
        return null;
      } catch (error: any) {
        console.error("Error fetching user: ", error);

        let errorMessage;
        if (error.response?.data?.response?.message) {
          errorMessage = error.response.data.response.message;
        } else if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        } else {
          errorMessage = String(error);
        }

        setError(error instanceof Error ? error : new Error(errorMessage));
        return null;
      }
    }, []);

  const refreshUser = useCallback(async (): Promise<UserResponse | null> => {
    setError(null);
    const userData = await fetchCurrentUser();
    if (userData) {
      setUser(userData);
    } else {
      setUser(null);
    }
    return userData;
  }, [fetchCurrentUser]);

  useEffect(() => {
    const initAuth = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const token = getToken();

        if (token && !isTokenExpired()) {
          const userData = await fetchCurrentUser();
          if (userData) {
            setUser(userData);
          } else {
            removeToken();
            setUser(null);
          }
        } else {
          removeToken();
          setUser(null);
        }
      } catch (error: any) {
        console.error("Error initializing auth:", error);

        let errorMessage;
        if (error.response?.data?.response?.message) {
          errorMessage = error.response.data.response.message;
        } else if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        } else {
          errorMessage = String(error);
        }

        setError(error instanceof Error ? error : new Error(errorMessage));
        removeToken();
        setUser(null);
      } finally {
        setTimeout(() => {
          setIsLoading(false);
        }, 300);
      }
    };

    initAuth();
  }, [fetchCurrentUser]);

  const login = useCallback(
    async (token: string, expiresIn: number): Promise<void> => {
      setError(null);
      setIsLoading(true);
      try {
        setToken(token, expiresIn);
        const userData = await refreshUser();
        if (!userData) {
          throw new Error("Failed to fetch user data after login");
        }
      } catch (error: any) {
        let errorMessage;
        if (error.response?.data?.response?.message) {
          errorMessage = error.response.data.response.message;
        } else if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        } else {
          errorMessage = String(error);
        }

        setError(error instanceof Error ? error : new Error(errorMessage));
        removeToken();
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    },
    [refreshUser],
  );

  const logout = useCallback(async (): Promise<void> => {
    setIsLoading(true);
    setError(null);
    try {
      if (getToken()) {
        await authApi.logout();
      }
    } catch (error: any) {
      console.error("Error during logout:", error);

      let errorMessage;
      if (error.response?.data?.response?.message) {
        errorMessage = error.response.data.response.message;
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else {
        errorMessage = String(error);
      }

      setError(error instanceof Error ? error : new Error(errorMessage));
    } finally {
      removeToken();
      setUser(null);
      setIsLoading(false);
    }
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        isLoading,
        login,
        logout,
        refreshUser,
        error,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
