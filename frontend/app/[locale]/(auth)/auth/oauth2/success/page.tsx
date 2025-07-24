"use client";

import {useEffect} from "react";
import {useRouter, useSearchParams} from "next/navigation";
import {useAuth} from "@/lib/auth/AuthContext";
import {Spinner} from "@/components/common";

// TODO: consider moving this to oauth2/success/ instead of /auth/oauth2/....
const OAuth2Success = () => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const {login, isAuthenticated} = useAuth();

  useEffect(() => {
    const token = searchParams?.get("token");
    const expiresIn = searchParams?.get("expiresIn");

    if (token && expiresIn) {
      try {
        const expiresInMs = parseInt(expiresIn, 10);

        login(token, expiresInMs);

        const redirectTimer = setTimeout(() => {
          router.push("/");
        }, 1500);

        return () => clearTimeout(redirectTimer);
      } catch (err) {
        console.error("Error processing OAuth login", err);
      }
    } else {
      const redirectTimer = setTimeout(() => {
        router.push("/login");
      }, 3000);

      return () => clearTimeout(redirectTimer);
    }
  }, [searchParams, router, login]);

  // TODO: this can be handled better???
  if (isAuthenticated) {
    return <Spinner fullPage={true} />;
  }

  if (!isAuthenticated) {
    return <Spinner fullPage={true} />;
  }
};

export default OAuth2Success;
