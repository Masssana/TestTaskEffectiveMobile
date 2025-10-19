package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardDTO;
import com.example.bankcards.dto.FilterRequest;
import com.example.bankcards.dto.RequestToCardCreate;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private PersonService personService;

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .build();
    }

    @Test
    public void getBalanceShouldReturnBalance() throws Exception {
        when(personService.viewBalance(1L)).thenReturn(new BigDecimal(1000));

        mockMvc.perform(get("/api/user/view/balance/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));

        verify(personService, times(1)).viewBalance(1L);
    }

    @Test
    public void send_shouldReturnConfirmationMessage() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setCardNumberFrom("1234 5678 9012 3456");
        request.setCardNumberTo("2345 5678 9012 3456");
        request.setMoney(new BigDecimal(100));

        when(personService.sendMoney(any(TransferRequest.class))).thenReturn("Sent successfully");

        mockMvc.perform(post("/api/user/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Sent successfully"));

        verify(personService, times(1)).sendMoney(any(TransferRequest.class));
    }

    @Test
    void sendBlockRequest_shouldCallService() throws Exception {
        doNothing().when(personService).requestToBlockTheCard(1L);

        mockMvc.perform(post("/api/user/send/block/request/1"))
                .andExpect(status().isOk());

        verify(personService, times(1)).requestToBlockTheCard(1L);
    }

    @Test
    void sendCreateRequest_shouldCallService() throws Exception {
        RequestToCardCreate request = new RequestToCardCreate();
        request.setLabel("New Card");

        doNothing().when(personService).requestToCreateTheCard(any(RequestToCardCreate.class));

        mockMvc.perform(post("/api/user/send/create/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(personService, times(1)).requestToCreateTheCard(any(RequestToCardCreate.class));
    }
}
