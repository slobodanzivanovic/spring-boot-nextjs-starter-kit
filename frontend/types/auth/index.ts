export type RegisterRequest = {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
};

export type LoginRequest = {
  identifier: string;
  password: string;
};

export type VerifyRequest = {
  email: string;
  verificationCode: string;
};

export interface LoginResponse {
  token: string;
  expiresIn: number;
}

export type UserResponse = {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  profileImageUrl: string | null;
  roles: string[];
};

export type CustomResponse<T> = {
  time: string;
  httpStatus: string;
  isSuccess: boolean;
  response: T;
};

// TODO: move this and customresponse to its own files...
export type PagedResponse<T> = {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  data: T[];
};

// TODO: move this also and make sure it is used... we are not using this for now.,..
export type ErrorResponse = {
  message: string;
  errorCode: string;
  path: string;
  timestamp: string;
  errors?: Record<string, string>;
};
