package com.example.bankcards.service.filters;

import com.example.bankcards.dto.FilterRequest;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.service.BankCardSpecification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchCarFilter implements BankCardSpecification {

    @Override
    public Specification<BankCard> toSpecification(FilterRequest filterRequest) {
        String search = filterRequest.getSearch();
        if(search == null) {
            return null;
        }

        return (root, query, cb) -> {
            String pattern = "%" + search.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.like(cb.lower(root.get("cardNumber")), pattern));
            predicates.add(cb.like(cb.lower(root.get("label")), pattern));
            predicates.add(cb.like(cb.lower(root.get("status")), pattern));

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
