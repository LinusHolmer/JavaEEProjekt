package com.example.JavaEE.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(@Size(max = 50, message = "Maximum characters is 50")@NotBlank(message = "Username can not be Blank")
                                String username,
                                @Size(max = 50, message = "Maximum characters is 50")@NotBlank(message = "Password can not be Blank")
                                String newPassword) {
}
