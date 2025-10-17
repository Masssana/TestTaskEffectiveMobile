package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardDTO;
import com.example.bankcards.dto.PersonCreateRequest;
import com.example.bankcards.dto.PersonDTO;
import com.example.bankcards.dto.RequestDTO;

import java.util.List;

public interface AdminService {
    List<BankCardDTO> getAllBankCards();
    BankCardDTO getCardByNumber(Long cardId);
    void createBankCard(BankCardDTO bankCardDTO);
    void blockCard(Long cardId);
    void activateCard(Long cardId);
    void deleteCard(Long cardId);
    void makeUserAdmin(Long personId);
    void makeAdminUser(Long personId);
    List<PersonDTO> getAllUsers();
    void deleteUser(Long personId);
    void createUser(PersonCreateRequest personCreateRequest);
    void acceptTheRequest(Long requestId);
    void rejectTheRequest(Long requestId);
    List<RequestDTO> getAllRequests();
}
