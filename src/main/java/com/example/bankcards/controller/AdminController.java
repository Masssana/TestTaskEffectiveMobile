package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardDTO;
import com.example.bankcards.dto.PersonCreateRequest;
import com.example.bankcards.dto.PersonDTO;
import com.example.bankcards.dto.RequestDTO;
import com.example.bankcards.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/get/all")
    public ResponseEntity<List<BankCardDTO>> getAll() {
        return ResponseEntity.ok(adminService.getAllBankCards());
    }

    @GetMapping("/get/{cardId}")
    public ResponseEntity<BankCardDTO> getBankCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(adminService.getCardByNumber(cardId));
    }

    @PostMapping("/create/card")
    public void createBankCard(@RequestBody BankCardDTO bankCardDTO) {
        adminService.createBankCard(bankCardDTO);
    }

    @PutMapping("/block/{cardId}")
    public void blockCard(@PathVariable Long cardId) {
        adminService.blockCard(cardId);
    }

    @PutMapping("/activate/{cardId}")
    public void activateCard(@PathVariable Long cardId) {
        adminService.activateCard(cardId);
    }

    @DeleteMapping("/delete/{cardId}")
    public void deleteCard(@PathVariable Long cardId) {
        adminService.deleteCard(cardId);
    }

    @PutMapping("/make/admin/{userId}")
    public void makeAdmin(@PathVariable Long userId) {
        adminService.makeUserAdmin(userId);
    }

    @PutMapping("/make/user/{userId}")
    public void makeUser(@PathVariable Long userId) {
        adminService.makeAdminUser(userId);
    }

    @GetMapping("/get/users")
    public ResponseEntity<List<PersonDTO>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/delete/user/{userId}")
    public void deleteUser(@PathVariable Long userId){
        adminService.deleteUser(userId);
    }

    @PostMapping("/create/user")
    public void createUser(@RequestBody PersonCreateRequest personDTO) {
        adminService.createUser(personDTO);
    }

    @PostMapping("/accept/request/{requestId}")
    public void acceptTheRequest(@PathVariable Long requestId) {
        adminService.acceptTheRequest(requestId);
    }

    @PostMapping("/decline/request/{requestId}")
    public void declineTheRequest(@PathVariable Long requestId) {
        adminService.rejectTheRequest(requestId);
    }

    @GetMapping("/get/requests")
    public ResponseEntity<List<RequestDTO>> getRequests() {
        return ResponseEntity.ok(adminService.getAllRequests());
    }
}
