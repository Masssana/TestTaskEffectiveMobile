package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PersonService {
    BigDecimal viewBalance(Long cardId);
    Page<BankCardDTO> viewAll(Pageable pageable, FilterRequest filterRequest);
    String sendMoney(TransferRequest transferRequest);
    void requestToBlockTheCard(Long cardId);
    void requestToCreateTheCard(RequestToCardCreate request);
    List<RequestDTO> getAnswers();
}
