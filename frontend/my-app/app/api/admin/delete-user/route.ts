import { NextRequest, NextResponse } from "next/server";

// körs när frontend gör delete anrop /api/admin/delete-user
export async function DELETE(request: NextRequest) {

  // hämta jwt token från cookie
  const jwt = request.cookies.get("jwt")?.value;

  // hämta userid från request body
  const { userId } = await request.json();

  if (!jwt) {
    return NextResponse.json({ error: "No JWT provided" }, { status: 401 });
  }

  if (!userId) {
    return NextResponse.json({ error: "Missing userId" }, { status: 400 });
  }

  // skicka delete till spring backend
  const response = await fetch(`http://localhost:8080/admin/users/${userId}`, {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${jwt}`, // jwt token
    },
    cache: "no-store",
  });

  // backend svarar inte = 204
  if (!response.ok && response.status !== 204) {
    return NextResponse.json(
      { error: "Failed to delete user" },
      { status: response.status }
    );
  }

  // returnera "success" till frontend
  return NextResponse.json({ success: true });
}
