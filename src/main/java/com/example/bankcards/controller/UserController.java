package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardDTO;
import com.example.bankcards.dto.FilterRequest;
import com.example.bankcards.dto.RequestToCardCreate;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final PersonService personService;

    @GetMapping("/view/balance/{cardId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long cardId) {
        return ResponseEntity.ok(personService.viewBalance(cardId));
    }

    @GetMapping("/cards")
    public ResponseEntity<Page<BankCardDTO>> viewCards(Pageable pageable, FilterRequest filterRequest) {
        return ResponseEntity.ok(personService.viewAll(pageable, filterRequest));
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody TransferRequest transferRequest) {
        return ResponseEntity.ok(personService.sendMoney(transferRequest));
    }

    @PostMapping("/send/block/request/{cardId}")
    public void sendBlockRequest(@PathVariable Long cardId){
        personService.requestToBlockTheCard(cardId);
    }

    @PostMapping("/send/create/request")
    public void sendCreateRequest(@RequestBody RequestToCardCreate requestToCardCreate) {
        personService.requestToCreateTheCard(requestToCardCreate);
    }

}
