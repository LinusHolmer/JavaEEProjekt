"use client";
import { useState } from "react";
import CustomButton from "../components/CustomButton/CustomButton";
import { useRouter } from "next/navigation";

export default function LogoutPage() {
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  const router = useRouter();

  const logout = async () => {
    setError(null);
    setSuccess(false);

    try {
      const response = await fetch("http://localhost:8080/logout", {
        method: "POST",
        credentials: "include",
       
      });

       if (!response.ok) {
        const errorBody = await response.text();
        setError(errorBody || "Logout failed.");
        alert(error)
        return;
      }

      setSuccess(true);
      alert("Successfully logged out!")
      router.push("/login")

      

    } catch (error) {
      setError("Network error: backend unreachable");
      alert(error)
    }
  };

  return (
    <main>
        <CustomButton buttonText={"Logout"} onClick={logout}/>
    </main>
  );
}