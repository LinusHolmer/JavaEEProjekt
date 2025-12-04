"use client"
import { useRouter } from "next/navigation";
import CustomButton from "../components/CustomButton/CustomButton";


export default function AdminPage() {
  const router = useRouter();

    return (
    <main>
    <h1>ADMIN PAGE</h1>
    <CustomButton buttonText={"Change username"} onClick={() => router.push("/adminChangeUsername")}/>
    <CustomButton buttonText={"Change password"} onClick={() => router.push("/adminChangePassword")}/> 
    <CustomButton buttonText={"Change username"} onClick={() => router.push("/adminChangeUsername")}/>
    <CustomButton buttonText={"Change username"} onClick={() => router.push("/adminChangeUsername")}/>
    <CustomButton buttonText={"Change username"} onClick={() => router.push("/adminChangeUsername")}/>
    </main>
  );
  };

  