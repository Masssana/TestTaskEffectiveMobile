package com.example.bankcards.service.impl;

import com.example.bankcards.dto.BankCardDTO;
import com.example.bankcards.dto.PersonCreateRequest;
import com.example.bankcards.dto.PersonDTO;
import com.example.bankcards.dto.RequestDTO;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.Person;
import com.example.bankcards.entity.Request;
import com.example.bankcards.enums.RequestAnswer;
import com.example.bankcards.enums.RequestType;
import com.example.bankcards.enums.Role;
import com.example.bankcards.enums.Status;
import com.example.bankcards.exception.BankCardNotFoundException;
import com.example.bankcards.exception.RequestNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.PersonRepository;
import com.example.bankcards.repository.RequestRepository;
import com.example.bankcards.service.AdminService;
import com.example.bankcards.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final BankCardRepository bankCardRepository;
    private final PersonRepository personRepository;
    private final RequestRepository requestRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<BankCardDTO> getAllBankCards() {
        return bankCardRepository.findAll().stream().map(card -> {
            BankCardDTO dto = BankCardDTO.from(card);
            dto.setCardNumber(EncryptUtil.encrypt(card.getCardNumber()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public BankCardDTO getCardByNumber(Long cardId) {
        BankCard bankCard = bankCardRepository
                .findById(cardId)
                .orElseThrow(() -> new BankCardNotFoundException("Карта с таким номером не была найдена"));
        BankCardDTO dto  = BankCardDTO.from(bankCard);
        dto.setCardNumber(EncryptUtil.encrypt(bankCard.getCardNumber()));
        return dto;
    }

    @Override
    public void createBankCard(BankCardDTO bankCardDTO) {
        bankCardRepository.save(BankCard.from(bankCardDTO));
    }

    @Override
    public void blockCard(Long cardId) {
        BankCard bankCard = bankCardRepository
                .findById(cardId)
                .orElseThrow(() -> new BankCardNotFoundException("Карта с таким номером не была найдена"));
        bankCard.setStatus(Status.BLOCKED);
        bankCardRepository.save(bankCard);
    }

    @Override
    public void activateCard(Long cardId) {
        BankCard bankCard = bankCardRepository
                .findById(cardId)
                .orElseThrow(() -> new BankCardNotFoundException("Карта с таким номером не была найдена"));
        bankCard.setStatus(Status.ACTIVE);
    }

    @Override
    public void deleteCard(Long cardId) {
        BankCard bankCard = bankCardRepository
                .findById(cardId)
                .orElseThrow(() -> new BankCardNotFoundException("Карта с таким номером не была найдена"));
        bankCardRepository.delete(bankCard);
    }

    @Override
    public void makeUserAdmin(Long personId){
        Person person = personRepository.
                findById(personId)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));

        person.setRole(Role.ADMIN);
        personRepository.save(person);
    }

    @Override
    public void makeAdminUser(Long personId){
        Person person = personRepository.
                findById(personId)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));

        person.setRole(Role.USER);
        personRepository.save(person);
    }

    @Override
    public List<PersonDTO> getAllUsers() {
        return personRepository.findAll().stream().map(PersonDTO::from).toList();
    }

    @Override
    public void deleteUser(Long personId) {
        Person person = personRepository.
                findById(personId)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));
        personRepository.delete(person);
    }

    @Override
    public void createUser(PersonCreateRequest personCreateRequest) {
        Person person = Person.builder()
                .firstName(personCreateRequest.getFirstName())
                .lastName(personCreateRequest.getLastName())
                .email(personCreateRequest.getEmail())
                .role(personCreateRequest.getRole())
                .password(passwordEncoder.encode(personCreateRequest.getPassword()))
                .build();
        personRepository.save(person);
    }

    @Override
    public void acceptTheRequest(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException("Такого запроса не существует"));
        request.setRequestType(RequestType.CREATE);
        request.setRequestAnswer(RequestAnswer.ACCEPTED);
        requestRepository.save(request);
    }

    @Override
    public void rejectTheRequest(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException("Такого запроса не существует"));
        request.setRequestType(RequestType.BLOCk);
        request.setRequestAnswer(RequestAnswer.DECLINED);
        requestRepository.save(request);
    }

    @Override
    public List<RequestDTO> getAllRequests() {
        return requestRepository.findAll().stream().map(RequestDTO::fromRequest).toList();
    }
}
