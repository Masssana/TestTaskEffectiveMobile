package com.example.bankcards.entity;

import com.example.bankcards.dto.RequestToCardCreate;
import com.example.bankcards.enums.RequestAnswer;
import com.example.bankcards.enums.RequestType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    private RequestAnswer requestAnswer;

    private String firstName;

    private String lastName;

    private String email;
}
