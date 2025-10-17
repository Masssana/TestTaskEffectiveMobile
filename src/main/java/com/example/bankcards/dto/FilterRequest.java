package com.example.bankcards.dto;

import com.example.bankcards.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Фильтр")
public class FilterRequest {
    private Status status;
    private BigDecimal balance;
    private String search;
}
