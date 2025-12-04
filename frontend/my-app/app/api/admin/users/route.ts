
import { NextRequest, NextResponse } from "next/server";

export async function GET(request: NextRequest) {
  const jwt = request.cookies.get("jwt")?.value;

  const response = await fetch("http://localhost:8080/admin/users", {
    headers: { Authorization: `Bearer ${jwt}` },
    cache: "no-store",
  });

  const data = await response.json();
  return NextResponse.json(data);
}
