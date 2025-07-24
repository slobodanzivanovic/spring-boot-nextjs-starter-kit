export const API_BASE_URL = process.env.NEXT_PUBLIC_BASE_URL;

export const API_ENDPOINTS = {
  AUTH: {
    REGISTER: "/api/v1/auth/register",
    VERIFY: "/api/v1/auth/verify",
    RESEND_VERIFICATION: "/api/v1/auth/resend-verification",
    LOGIN: "/api/v1/auth/login",
    OAUTH_GOOGLE: "/api/v1/auth/oauth2/authorization/google",
    OAUTH_GITHUB: "/api/v1/auth/oauth2/authorization/github",
    LOGOUT: "/api/v1/auth/logout",
    REQUEST_PASSWORD_RESET: "/api/v1/auth/request-password-reset",
    RESET_PASSWORD: "/api/v1/auth/reset-password",
  },
  USERS: {
    CURRENT: "/api/v1/users/me",
    UPDATE_PROFILE_IMAGE: "/api/v1/users/profile-image",
  },
  ADMIN: {
    USERS: "/api/v1/admin/users",
  },
};
