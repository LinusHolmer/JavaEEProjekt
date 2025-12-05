"use client";
import { useRouter } from "next/navigation";
import CustomButton from "../components/CustomButton/CustomButton";
import styles from "../login/login.module.css";
import { useState } from "react";

export default function AdminPage() {
  const [username, setUsername] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  const router = useRouter();

  const login = async () => {
    setError(null);
    setSuccess(false);

    try {
      const response = await fetch("/api/adminChangePassword", {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, newPassword }),
      });

      if (!response.ok) {
        const errorBody = await response.text();
        setError(errorBody || "Change password failed.");
        alert(error)
        return;
      }

      setSuccess(true);
      alert("Successfully changed password!");
      router.push("/");
    } catch (error) {
      setError("Network error: backend unreachable");
      alert(error)
    }
  };

  return (
    <main className={styles.main}>
      <form className={styles.form} onSubmit={(e) => e.preventDefault()}>
        <label className={styles.label}>Username</label>
        <input
          className={styles.input}
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <label className={styles.label}>New Password</label>
        <input
          className={styles.input}
          type="password"
          placeholder="New Password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
        />

        <CustomButton buttonText={"Change Password"} onClick={login} />
        {error && <p style={{ color: "red" }}>{error}</p>}
      </form>
    </main>
  );
}
