import {useAuth} from "@/lib/auth/AuthContext";
import {useState} from "react";
import {LoginRequest, RegisterRequest, VerifyRequest} from "@/types/auth";
import {authApi} from "@/constants/api/api";

export function useAuthFunctions() {
  const {login: contextLogin} = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const extractErrorMessage = (err: any): string => {
    if (err.response?.data?.response?.message) {
      return err.response.data.response.message;
    }

    if (err.response?.data?.response?.errors) {
      const errors = err.response.data.response.errors;
      return Object.values(errors).join(", ");
    }

    if (err.response?.data?.message) {
      return err.response.data.message;
    }

    return "An error occurred. Please try again.";
  };

  const loginWithCredentials = async (data: LoginRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await authApi.login(data);

      if (response.data.isSuccess && response.data.response) {
        const {token, expiresIn} = response.data.response;
        await contextLogin(token, expiresIn);
        return true;
      } else {
        setError("Login failed. Please check your credentials.");
        return false;
      }
    } catch (err: any) {
      setError(extractErrorMessage(err));
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (data: RegisterRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await authApi.register(data);

      return response.data.isSuccess;
    } catch (err: any) {
      setError(extractErrorMessage(err));
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const verifyAccount = async (data: VerifyRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await authApi.verify(data);

      return response.data.isSuccess;
    } catch (err: any) {
      setError(extractErrorMessage(err));
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const requestPasswordReset = async (email: string) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await authApi.requestPasswordReset(email);

      return response.data.isSuccess;
    } catch (err: any) {
      setError(extractErrorMessage(err));
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const resetPassword = async (
    email: string,
    verificationCode: string,
    newPassword: string,
  ) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await authApi.resetPassword(
        email,
        verificationCode,
        newPassword,
      );

      return response.data.isSuccess;
    } catch (err: any) {
      setError(extractErrorMessage(err));
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const resendVerification = async (email: string) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await authApi.resendVerification(email);

      return response.data.isSuccess;
    } catch (err: any) {
      setError(extractErrorMessage(err));
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    loginWithCredentials,
    register,
    verifyAccount,
    requestPasswordReset,
    resetPassword,
    resendVerification,
    isLoading,
    error,
  };
}
