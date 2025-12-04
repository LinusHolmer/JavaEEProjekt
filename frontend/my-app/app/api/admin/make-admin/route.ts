import { NextRequest, NextResponse } from "next/server";

// körs när frontend gör post
export async function POST(request: NextRequest) {
  const jwt = request.cookies.get("jwt")?.value;
  const { userId } = await request.json(); 

  if (!jwt) {
    return NextResponse.json({ error: "No JWT provided" }, { status: 401 });
  }

  if (!userId) {
    return NextResponse.json({ error: "Missing userId" }, { status: 400 });
  }

  const response = await fetch(
    `http://localhost:8080/admin/make-admin/${userId}`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
      cache: "no-store",
    }
  );

  if (!response.ok) {
    return NextResponse.json(
      { error: "Failed to promote user" },
      { status: response.status }
    );
  }

  const data = await response.json();
  return NextResponse.json(data);
}
