package com.example.bankcards.service;

import com.example.bankcards.dto.FilterRequest;
import com.example.bankcards.entity.BankCard;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface BankCardSpecification {
    Specification<BankCard> toSpecification(FilterRequest filterRequest);
}
