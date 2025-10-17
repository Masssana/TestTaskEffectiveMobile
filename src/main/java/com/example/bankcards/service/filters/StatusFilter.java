package com.example.bankcards.service.filters;

import com.example.bankcards.dto.FilterRequest;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.service.BankCardSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class StatusFilter implements BankCardSpecification {
    @Override
    public Specification<BankCard> toSpecification(FilterRequest filterRequest) {
        if(filterRequest.getStatus() == null){
            return null;
        }
        return (root, cq, cb) -> cb.equal(root.get("status"), filterRequest.getStatus());
    }
}
