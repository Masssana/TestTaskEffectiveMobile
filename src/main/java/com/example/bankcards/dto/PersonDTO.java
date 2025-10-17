package com.example.bankcards.dto;

import com.example.bankcards.entity.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Пользователь")
public class PersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    @Email
    private String email;

    public static PersonDTO from(Person person) {
        return PersonDTO.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .email(person.getEmail())
                .build();
    }

    public static Person toEntity(PersonDTO dto) {
        if (dto == null) return null;
        return Person.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();
    }

}
