package com.example.bankcards.service;

import com.example.bankcards.dto.FilterRequest;
import com.example.bankcards.entity.BankCard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BankCardSpecificationBuilder {
    private final List<BankCardSpecification> specs;

    public Specification<BankCard> build(FilterRequest filterRequest){
        return specs.stream().map(c -> {
            return c.toSpecification(filterRequest);
        }).filter(Objects::nonNull).reduce(Specification::and).orElse(null);
    }
}
