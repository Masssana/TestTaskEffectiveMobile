package com.example.bankcards.dto;

import com.example.bankcards.entity.Request;
import com.example.bankcards.enums.RequestAnswer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на создание или блокировку")
public class RequestDTO {
    private Long id;
    private RequestAnswer answer;

    private String label;

    private PersonDTO person;

    public static RequestDTO fromRequest(Request request) {
        if (request == null) {
            return null;
        }
        return RequestDTO.builder()
                .id(request.getId())
                .label(request.getLabel())
                .answer(request.getRequestAnswer())
                .person(PersonDTO.from(request.getPerson()))
                .build();
    }
}
