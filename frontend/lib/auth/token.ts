import Cookies from "js-cookie";

const TOKEN_KEY = "access_token";
const TOKEN_EXPIRY_KEY = "token_expiry";

export function getToken(): string | undefined {
  return Cookies.get(TOKEN_KEY);
}

export function setToken(token: string, expiresIn: number) {
  if (isNaN(expiresIn) || expiresIn <= 0) {
    expiresIn = 24 * 60 * 60 * 1000;
  }

  const expiryDate = new Date(Date.now() + expiresIn);
  const expiryInDays = Math.floor(expiresIn / (1000 * 60 * 60 * 24));

  Cookies.set(TOKEN_KEY, token, {
    expires: expiryInDays > 0 ? expiryInDays : 1,
    path: "/",
    sameSite: "strict",
    secure: window.location.protocol === "https:",
  });

  Cookies.set(TOKEN_EXPIRY_KEY, expiryDate.getTime().toString(), {
    expires: expiryInDays > 0 ? expiryInDays : 1,
    path: "/",
    sameSite: "strict",
    secure: window.location.protocol === "https:",
  });
}

export function isTokenExpired(): boolean {
  const expiry = Cookies.get(TOKEN_EXPIRY_KEY);
  if (!expiry) return true;

  const expiryTime = parseInt(expiry, 10);
  if (isNaN(expiryTime)) return true;

  return Date.now() > expiryTime;
}

export function removeToken(): void {
  Cookies.remove(TOKEN_KEY, {path: "/"});
  Cookies.remove(TOKEN_EXPIRY_KEY, {path: "/"});
}
