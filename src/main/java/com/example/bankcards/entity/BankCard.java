package com.example.bankcards.entity;

import com.example.bankcards.dto.BankCardDTO;
import com.example.bankcards.dto.PersonDTO;
import com.example.bankcards.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cardNumber;

    private String label;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person owner;

    @Builder.Default
    private LocalDateTime expiryDate = LocalDateTime.now().plusYears(5);

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    public static BankCard from(BankCardDTO bankCardDTO){
        return BankCard.builder()
                .cardNumber(bankCardDTO.getCardNumber())
                .label(bankCardDTO.getLabel())
                .owner(PersonDTO.toEntity(bankCardDTO.getOwner()))
                .expiryDate(bankCardDTO.getExpiryDate())
                .status(bankCardDTO.getStatus())
                .balance(bankCardDTO.getBalance())
                .build();
    }
}
