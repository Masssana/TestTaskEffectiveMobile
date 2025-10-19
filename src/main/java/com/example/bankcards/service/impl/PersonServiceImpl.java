package com.example.bankcards.service.impl;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.Person;
import com.example.bankcards.entity.Request;
import com.example.bankcards.enums.RequestType;
import com.example.bankcards.exception.BankCardNotFoundException;
import com.example.bankcards.exception.NotCardOwnerException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.PersonRepository;
import com.example.bankcards.repository.RequestRepository;
import com.example.bankcards.service.BankCardSpecificationBuilder;
import com.example.bankcards.service.PersonService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final BankCardRepository bankCardRepository;
    private final BankCardSpecificationBuilder bankCardSpecificationBuilder;
    private final RequestRepository requestRepository;

    @Override
    public BigDecimal viewBalance(Long cardId) {
        BankCard bankCard = bankCardRepository
                .findById(cardId)
                .orElseThrow(() -> new BankCardNotFoundException("Такой карты не существует"));
        checkIfOwner(bankCard);
        return bankCard.getBalance();
    }

    private void checkIfOwner(BankCard bankCard) {
        if(!bankCard.getOwner().getEmail().equals(getUserEmail())){
            throw new NotCardOwnerException("Вы не владелец данной карты");
        }
    }

    @Override
    public Page<BankCardDTO> viewAll(Pageable pageable, FilterRequest filterRequest) {
        Person currentPerson = getPerson();

        var query = bankCardSpecificationBuilder.build(filterRequest);

        if (query == null) {
            query = (root, cq, cb) -> cb.conjunction(); // вернёт все записи
        }

        query = query
                .and(((root, query1, cb) ->  cb.equal(root.get("owner").get("id"), currentPerson.getId())));
        return bankCardRepository.findAll(query, pageable).map(BankCardDTO::from);
    }

    @Override
    public void requestToBlockTheCard(Long cardId) {
        Request request = Request.builder().requestType(RequestType.BLOCk).build();
        requestRepository.save(request);
    }

    @Override
    public void requestToCreateTheCard(RequestToCardCreate requestToCardCreate){
        Request request = Request.builder()
                .requestType(RequestType.CREATE)
                .label(requestToCardCreate.getLabel())
                .firstName(requestToCardCreate.getFirstName())
                .lastName(requestToCardCreate.getLastName())
                .email(requestToCardCreate.getEmail())
                .build();
        requestRepository.save(request);
    }

    @Override
    public List<RequestDTO> getAnswers(){
        // TODO сделать проверку что реквесты юзера
        Person currentPerson = getPerson();
        return requestRepository.findAllByPersonId(currentPerson.getId()).stream().map(RequestDTO::fromRequest).toList();
    }

    @Transactional
    @Override
    public String sendMoney(TransferRequest transferRequest) {
        if(transferRequest.getMoney() == null || transferRequest.getMoney().compareTo(BigDecimal.ZERO) <= 0 ) {
            return "Сумма перевода должна быть больше 0";
        }
        BankCard cardFrom = bankCardRepository.findByCardNumber(transferRequest.getCardNumberFrom()).orElseThrow(() -> new BankCardNotFoundException("Карта отправителя не найдена"));
        BankCard cardTo = bankCardRepository.findByCardNumber(transferRequest.getCardNumberTo()).orElseThrow(() -> new BankCardNotFoundException("Карта получателя не найдена"));

        if (cardFrom.getCardNumber().equals(cardTo.getCardNumber())) {
            return "Нельзя перевести деньги на ту же карту";
        }

        if(cardFrom.getBalance() == null || cardFrom.getBalance().compareTo(transferRequest.getMoney()) < 0) {
            return "Недостаточно средств на карте отправителя";
        }

        cardFrom.setBalance(cardFrom.getBalance().subtract(transferRequest.getMoney()));
        cardTo.setBalance(cardTo.getBalance().add(transferRequest.getMoney()));
        bankCardRepository.save(cardFrom);
        bankCardRepository.save(cardTo);
        return "Операция перевода прошла успешно";
    }

    private Person getPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Пользователь не аутенцифицирован");
        }

        String email = null;
        Object principal = auth.getPrincipal();
        if(principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }else if(principal instanceof String) {
            email = (String) principal;
        }

        if(email == null) {
            throw new IllegalStateException("Не удалость определить пользователя");
        }

        return personRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    private String getUserEmail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Пользователь не аутентифицирован");
        }
        return auth.getName();
    }
}
