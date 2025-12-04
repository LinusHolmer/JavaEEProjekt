"use client"; 

import { useEffect, useState } from "react";

//matchar vad backend skickar
type User = {
  id: string;
  username: string;
  roles: string[];
};


export default function AdminPage() {
  // state som håller alla users från backend
  const [users, setUsers] = useState<User[]>([]);

  // visar laddnings status
  const [loading, setLoading] = useState(true);

  // visar felmeddelanden om något går fel
  const [error, setError] = useState<string | null>(null);

  // visar loading… bara för den user som klickats på
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);

  // hämtar alla users från vårt next API
  async function loadUsers() {
    try {
      setLoading(true);
      setError(null);

      // hämtar data från next api, sen pratar med spring i backnd
      const res = await fetch("/api/admin/users", {
        method: "GET",
      });

      // felmeddelande om det blir fel i backend
      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.error || "Kunde inte hämta users");
      }

      // rätt = spara users i state
      const data = await res.json();
      setUsers(data);
    } catch (err: any) {
      setError(err.message ?? "Något gick fel");
    } finally {
      setLoading(false);
    }
  }

  // useffect körs 1 gång när sidan laddas, hämtar användare direkt
  useEffect(() => {
    loadUsers();
  }, []);

  // gör en användare till admin
  async function handleMakeAdmin(userId: string) {
    try {
      setActionLoadingId(userId);

      // skickar userid i body till vårt next api
      const res = await fetch("/api/admin/make-admin", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId }),
      });

      

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.error || "Kunde inte göra admin");
      }

      // när backend uppdaterar ersätts användaren i listan
      const updatedUser: User = await res.json();
      setUsers((prev) =>
        prev.map((u) => (u.id === updatedUser.id ? updatedUser : u)),
      );
    } catch (err: any) {
      alert(err.message ?? "Något gick fel");
    } finally {
      setActionLoadingId(null);
    }
  }

  // ta bort en användare (DELETE)
  async function handleDeleteUser(userId: string) {
    // fråga så att man inte råkar ta bort en användare
    if (!confirm("Är du säker på att du vill ta bort den här användaren?")) {
      return;
    }

    try {
      setActionLoadingId(userId);

      const res = await fetch("/api/admin/delete-user", {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId }),
      });

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.error || "Kunde inte ta bort användaren");
      }

      // tar bort användaren från listan i state
      setUsers((prev) => prev.filter((u) => u.id !== userId));
    } catch (err: any) {
      alert(err.message ?? "Något gick fel");
    } finally {
      setActionLoadingId(null);
    }
  }

  // UI om sidan håller på att ladda
  if (loading) {
    return <p style={{ padding: 24 }}>Laddar användare...</p>;
  }

  // UI om något fel inträffade
  if (error) {
    return (
      <div style={{ padding: 24 }}>
        <h1>Admin – Användare</h1>
        <p style={{ color: "red" }}>{error}</p>
        <button onClick={loadUsers}>Försök igen</button>
      </div>
    );
  }

  // UI när allt är bra och users är laddade
  return (
    <main style={{ padding: 24, maxWidth: 800, margin: "0 auto" }}>
      <h1>Admin – Användare</h1>
      {users.length === 0 && <p>Inga användare hittades.</p>}

      {/* visar varje användare som en rad i listan */}
      <div style={{ marginTop: 16, display: "flex", flexDirection: "column", gap: 8 }}>
        {users.map((user) => {
          const isAdmin = user.roles?.includes("ROLE_ADMIN");

          return (
            <div
              key={user.id}
              style={{
                border: "1px solid #ddd",
                borderRadius: 8,
                padding: 12,
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              {/* vänstra delen som är user info */}
              <div>
                <strong>{user.username}</strong>
                <div style={{ fontSize: 12, color: "#555" }}>
                  Roller: {user.roles?.join(", ") || "Inga"}
                </div>
              </div>

              {/* högra delen, knappar */}
              <div style={{ display: "flex", gap: 8 }}>
                {/* visa bara "Gör admin" om de inte redan är admin */}
                {!isAdmin && (
                  <button
                    onClick={() => handleMakeAdmin(user.id)}
                    disabled={actionLoadingId === user.id}
                  >
                    {actionLoadingId === user.id ? "Gör admin..." : "Gör admin"}
                  </button>
                )}

                <button
                  onClick={() => handleDeleteUser(user.id)}
                  disabled={actionLoadingId === user.id}
                  style={{ backgroundColor: "#f44336", color: "white" }}
                >
                  {actionLoadingId === user.id ? "Tar bort..." : "Ta bort"}
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </main>
  );
}
