"use client";
import { useEffect, useState } from "react";

export default function UserPage() {
  const [users, setUsers] = useState<any[]>([]);

  useEffect(() => {
    fetch("/api/user", { credentials: "include" })
      .then(response => response.json())
      .then(setUsers);
  }, []); 

  return (
    <main>
      <h1>User Page</h1>
      <ul>
        {users.map((user, i) => (
          <li key={i}>{user.username}: {user.roles}</li>
        ))}
      </ul>
    </main>
  );
}
