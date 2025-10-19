package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "запрос на перевод денег")
public class TransferRequest {
    @NotBlank
    String cardNumberFrom;
    @NotBlank
    String cardNumberTo;
    BigDecimal money;
}
