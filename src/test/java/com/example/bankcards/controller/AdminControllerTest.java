package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardDTO;
import com.example.bankcards.dto.PersonCreateRequest;
import com.example.bankcards.dto.PersonDTO;
import com.example.bankcards.dto.RequestDTO;
import com.example.bankcards.enums.Status;
import com.example.bankcards.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WithMockUser(authorities = "ADMIN")
public class AdminControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    BankCardDTO card;
    @BeforeEach
    void setUp() {
        card = BankCardDTO.builder()
                .cardNumber("1234 5678 9012 3456")
                .label("Основная карта")
                .balance(new BigDecimal(1000))
                .status(Status.ACTIVE)
                .owner(new PersonDTO(1L, "Ivan", "Ivanov", "ivan@example.com", new ArrayList<>()))
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    public void getAllShouldReturnListOfBankCards() throws Exception {
        BankCardDTO card2 = BankCardDTO.builder()
                .cardNumber("4321 8756 9012 3456")
                .label("Основная карта")
                .balance(new BigDecimal(1000))
                .status(Status.ACTIVE)
                .owner(new PersonDTO(2L, "Vova", "Ivanov", "vova@example.com", new ArrayList<>()))
                .build();

        when(adminService.getAllBankCards()).thenReturn(List.of(card, card2));

        mockMvc.perform(get("/api/admin/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].cardNumber").value("1234 5678 9012 3456"))
                .andExpect(jsonPath("$[1].cardNumber").value("4321 8756 9012 3456"));
        verify(adminService, times(1)).getAllBankCards();

    }

    @Test
    void getBankCard_shouldReturnCard() throws Exception {
        when(adminService.getCardByNumber(1L)).thenReturn(card);

        mockMvc.perform(get("/api/admin/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("1234 5678 9012 3456"));

        verify(adminService, times(1)).getCardByNumber(1L);
    }

    @Test
    void createBankCard_shouldCallService() throws Exception {
        mockMvc.perform(post("/api/admin/create/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card)))
                .andExpect(status().isOk());

        verify(adminService, times(1)).createBankCard(any(BankCardDTO.class));
    }

    @Test
    void blockCard_shouldCallService() throws Exception {
        mockMvc.perform(put("/api/admin/block/1"))
                .andExpect(status().isOk());

        verify(adminService, times(1)).blockCard(1L);
    }

    @Test
    void activateCard_shouldCallService() throws Exception {
        mockMvc.perform(put("/api/admin/activate/1"))
                .andExpect(status().isOk());

        verify(adminService, times(1)).activateCard(1L);
    }

    @Test
    void deleteCard_shouldCallService() throws Exception {
        mockMvc.perform(delete("/api/admin/delete/1"))
                .andExpect(status().isOk());

        verify(adminService, times(1)).deleteCard(1L);
    }

    @Test
    void makeAdmin_shouldCallService() throws Exception {
        mockMvc.perform(put("/api/admin/make/admin/1"))
                .andExpect(status().isOk());

        verify(adminService, times(1)).makeUserAdmin(1L);
    }

    @Test
    void makeUser_shouldCallService() throws Exception {
        mockMvc.perform(put("/api/admin/make/user/1"))
                .andExpect(status().isOk());

        verify(adminService, times(1)).makeAdminUser(1L);
    }

    @Test
    void getUsers_shouldReturnList() throws Exception {
        PersonDTO person1 = new PersonDTO(1L, "Ivan", "Ivanov", "ivan@example.com", new ArrayList<>());
        PersonDTO person2 = new PersonDTO(2L, "Vova", "Petrov", "vova@example.com", new ArrayList<>());
        when(adminService.getAllUsers()).thenReturn(List.of(person1, person2));

        mockMvc.perform(get("/api/admin/get/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Ivan"))
                .andExpect(jsonPath("$[1].firstName").value("Vova"));

        verify(adminService, times(1)).getAllUsers();
    }

    @Test
    void deleteUser_shouldCallService() throws Exception {
        mockMvc.perform(delete("/api/admin/delete/user/1"))
                .andExpect(status().isOk());

        verify(adminService, times(1)).deleteUser(1L);
    }

    @Test
    void createUser_shouldCallService() throws Exception {
        PersonCreateRequest request = new PersonCreateRequest();
        request.setFirstName("Vova");
        request.setLastName("Petrov");
        request.setEmail("vova@example.com");
        request.setPassword("123");

        mockMvc.perform(post("/api/admin/create/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService, times(1)).createUser(any(PersonCreateRequest.class));
    }

    @Test
    void acceptTheRequest_shouldCallService() throws Exception {
        mockMvc.perform(post("/api/admin/accept/request/1"))
                .andExpect(status().isOk());

        verify(adminService, times(1)).acceptTheRequest(1L);
    }

    @Test
    void declineTheRequest_shouldCallService() throws Exception {
        mockMvc.perform(post("/api/admin/decline/request/1"))
                .andExpect(status().isOk());

        verify(adminService, times(1)).rejectTheRequest(1L);
    }

    @Test
    void getRequests_shouldReturnList() throws Exception {
        RequestDTO req1 = new RequestDTO(); req1.setId(1L);
        RequestDTO req2 = new RequestDTO(); req2.setId(2L);
        when(adminService.getAllRequests()).thenReturn(List.of(req1, req2));

        mockMvc.perform(get("/api/admin/get/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(adminService, times(1)).getAllRequests();
    }


}
