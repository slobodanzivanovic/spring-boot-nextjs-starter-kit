"use client";

import {useEffect} from "react";
import {useRouter, useSearchParams} from "next/navigation";

const OAuth2Success = () => {
  const router = useRouter();
  const searchParams = useSearchParams();

  useEffect(() => {
    const token = searchParams.get("token");
    const expiresIn = searchParams.get("expiresIn");

    if (token) {
      localStorage.setItem("token", token);

      if (expiresIn) {
        localStorage.setItem(
          "token_expiry",
          String(Date.now() + parseInt(expiresIn)),
        );
      }

      console.log("Successfully authenticated with OAuth2!");
      console.log("JWT Token:", token);

      router.push("/dashboard"); // it will return 404 not found dont worry biatch
    }
  }, [searchParams, router]);

  return <div>Processing login, please wait...</div>;
};

export default OAuth2Success;
