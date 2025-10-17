package com.example.bankcards.dto;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.Person;
import com.example.bankcards.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Банковская карта")
public class BankCardDTO {
    private String cardNumber;

    private String label;

    private PersonDTO owner;

    private LocalDateTime expiryDate;

    private Status status;

    private BigDecimal balance;

    public static BankCardDTO from(BankCard bankCard) {
        return BankCardDTO.builder()
                .cardNumber(bankCard.getCardNumber())
                .label(bankCard.getLabel())
                .owner(PersonDTO.from(bankCard.getOwner()))
                .expiryDate(bankCard.getExpiryDate())
                .status(bankCard.getStatus())
                .balance(bankCard.getBalance())
                .build();
    }
}
