package com.example.bankcards.service.filters;

import com.example.bankcards.dto.FilterRequest;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.service.BankCardSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BalanceFilter implements BankCardSpecification {

    @Override
    public Specification<BankCard> toSpecification(FilterRequest filterRequest) {
        if(filterRequest.getBalance() == null){
            return null;
        }
        return ((root, query, cb) -> cb.equal(root.get("balance"), filterRequest.getBalance()));
    }
}
