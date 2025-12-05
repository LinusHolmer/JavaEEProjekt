"use client";
import { useRouter } from "next/navigation";
import CustomButton from "../components/CustomButton/CustomButton";
import styles from "../login/login.module.css";
import { useState } from "react";

export default function AdminPage() {
  const [username, setUsername] = useState("");
  const [newUsername, setNewUsername] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  const router = useRouter();

  const login = async () => {
    setError(null);
    setSuccess(false);

    try {
      const response = await fetch("/api/adminChangeUsername", {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, newUsername }),
      });

      if (!response.ok) {
        const errorBody = await response.text();
        setError(errorBody || "Change username failed.");
        alert(error)
        return;
      }

      setSuccess(true);
      alert("Successfully changed username!");
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

        <label className={styles.label}>New Username</label>
        <input
          className={styles.input}
          type="text"
          placeholder="New Username"
          value={newUsername}
          onChange={(e) => setNewUsername(e.target.value)}
        />

        <CustomButton buttonText={"Change Username"} onClick={login} />
        {error && <p style={{ color: "red" }}>{error}</p>}
      </form>
    </main>
  );
}
