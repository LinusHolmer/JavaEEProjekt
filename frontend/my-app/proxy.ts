import { NextResponse, NextRequest } from "next/server";

export async function proxy(request: NextRequest) {
  
  const jwt = request.cookies.get("jwt")?.value

  if(!jwt) {
    return NextResponse.redirect(new URL("/login", request.url))
  }

  // console.log("COOKIE HEADER:", request.headers.get("cookie"))

  const response = await fetch("http://localhost:8080/checkRoles", {
    method: "GET",
    headers: { Authorization: `Bearer ${jwt}`},
    cache: 'no-store'
    
  })
  
  // console.log("BACKEND RESPONSE:", response.status)

  if(!response.ok) {
    return NextResponse.redirect(new URL("/login", request.url))
  }

  const data = await response.json();
  const roles:string[] = data.roles;

  

  if(request.nextUrl.pathname.startsWith("/admin") && !roles.includes("ADMIN")) {
    return NextResponse.redirect(new URL("/", request.url))
  }

  return NextResponse.next()

}
export const config = {
  matcher: ["/","/admin","/user", "/adminChangePassword", "/adminChangeUsername"],
};