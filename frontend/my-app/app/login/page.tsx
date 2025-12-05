"use client";
import { useState } from "react";
import styles from "../login/login.module.css"
import { useRouter } from "next/navigation";
import CustomButton from "../components/CustomButton/CustomButton"


export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  const router = useRouter();

  const login = async () => {
    setError(null);
    setSuccess(false);

    try {
      const response = await fetch("http://localhost:8080/login", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        const errorBody = await response.text();
        setError(errorBody || "Login failed.");
        alert(error)
        return;
      }

      setSuccess(true);
        alert("Successfully logged in!")
        router.push('/')
      
    } catch (error) {
      setError("Network error: backend unreachable");
      alert(error)
    }
  };

  return (
    <main className={styles.main}>
      <form className={styles.form}>
        <label className={styles.label}>Username</label>
        <input
          className={styles.input}
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <label className={styles.label}>Password</label>
        <input
          className={styles.input}
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <CustomButton buttonText={"Login"} onClick={login} />

      </form>
    </main>
  );
}