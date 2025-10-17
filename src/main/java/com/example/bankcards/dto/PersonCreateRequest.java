package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на создание пользователя")
public class PersonCreateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
