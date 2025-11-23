package com.example.Ecommerce.security.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequest {
    @NotBlank
    @Size(min=3,max = 20)
    private String username;

    @NotBlank
    @Email
    @Size(max = 30)
    private String email;
    private Set<String> roles;

    @NotBlank
    @Size(max = 40)
    private String password;
}

