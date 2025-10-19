package com.example.bankcards.entity;

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

    private String label;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    private RequestAnswer requestAnswer;

    private String firstName;

    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;

    @ManyToOne
    private Person person;
}
