"use client";

import styles from "../CustomButton/CustomButton.module.css";

interface CustomButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  buttonText: string;
  onClick: (e: React.MouseEvent<HTMLButtonElement>) => void;
}

export default function CustomButton({ buttonText, onClick, type = "button" }: CustomButtonProps) {
  return(
  <div className={styles.buttonContainer}>
    <button
      type={type}
      className={styles.button}
      onClick={onClick}
    >
      <p className={styles.buttonText}>{buttonText}</p>
    </button>

  </div>
  )
}