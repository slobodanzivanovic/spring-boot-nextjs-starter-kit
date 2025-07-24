import {
  CustomResponse,
  LoginRequest,
  LoginResponse,
  PagedResponse,
  RegisterRequest,
  UserResponse,
  VerifyRequest,
} from "@/types/auth";
import httpClient from "@/lib/httpClient";
import {API_BASE_URL, API_ENDPOINTS} from "@/constants/api/apiConfig";

export {API_BASE_URL, API_ENDPOINTS};

export const authApi = {
  register: (data: RegisterRequest) => {
    return httpClient.post<CustomResponse<void>>(
      API_ENDPOINTS.AUTH.REGISTER,
      data,
    );
  },

  verify: (data: VerifyRequest) => {
    return httpClient.post<CustomResponse<void>>(
      API_ENDPOINTS.AUTH.VERIFY,
      data,
    );
  },

  resendVerification: (email: string) => {
    return httpClient.post<CustomResponse<void>>(
      `${API_ENDPOINTS.AUTH.RESEND_VERIFICATION}?email=${encodeURIComponent(email)}`,
    );
  },

  login: (data: LoginRequest) => {
    return httpClient.post<CustomResponse<LoginResponse>>(
      API_ENDPOINTS.AUTH.LOGIN,
      data,
    );
  },

  logout: () => {
    return httpClient.post<CustomResponse<void>>(API_ENDPOINTS.AUTH.LOGOUT);
  },

  requestPasswordReset: (email: string) => {
    return httpClient.post<CustomResponse<void>>(
      `${API_ENDPOINTS.AUTH.REQUEST_PASSWORD_RESET}?email=${encodeURIComponent(email)}`,
    );
  },

  resetPassword: (
    email: string,
    verificationCode: string,
    newPassword: string,
  ) => {
    return httpClient.post<CustomResponse<void>>(
      `${API_ENDPOINTS.AUTH.RESET_PASSWORD}?email=${encodeURIComponent(email)}&verificationCode=${encodeURIComponent(verificationCode)}&newPassword=${encodeURIComponent(newPassword)}`,
    );
  },
};

export const userApi = {
  getCurrentUser: () => {
    return httpClient.get<CustomResponse<UserResponse>>(
      API_ENDPOINTS.USERS.CURRENT,
    );
  },

  updateProfileImage: (image: File) => {
    const formData = new FormData();
    formData.append("image", image);

    return httpClient.post<CustomResponse<UserResponse>>(
      API_ENDPOINTS.USERS.UPDATE_PROFILE_IMAGE,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      },
    );
  },
};

export const adminApi = {
  getUsers: (page: number = 0) => {
    return httpClient.get<CustomResponse<PagedResponse<UserResponse>>>(
      `${API_ENDPOINTS.ADMIN.USERS}?page=${page}`,
    );
  },
};
