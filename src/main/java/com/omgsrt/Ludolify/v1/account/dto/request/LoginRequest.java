package com.omgsrt.Ludolify.v1.account.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @Size(min = 1, message = "Username Must Be At Least 1 Character")
    String username;
    @Size(min = 8, message = "Password Must Be At Least 8 Characters")
    String password;
}
