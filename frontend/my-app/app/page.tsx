"use client"
import { useRouter } from "next/navigation";
import CustomButton from "./components/CustomButton/CustomButton";


export default function Home() {
  const router = useRouter();

  return (
    <div>
      <main>
        <CustomButton buttonText={"USER"} onClick={() => router.push("/user")}/>
        <CustomButton buttonText={"ADMIN"} onClick={() => router.push("/admin")}/>
      </main>
    </div>
  );
}
