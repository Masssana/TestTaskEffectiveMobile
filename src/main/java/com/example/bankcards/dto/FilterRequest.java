package com.example.bankcards.dto;

import com.example.bankcards.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(description = "Фильтр")
@AllArgsConstructor
@NoArgsConstructor
public class FilterRequest {
    private Status status;
    private BigDecimal balance;
    private String search;
}
