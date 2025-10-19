package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.Person;
import com.example.bankcards.entity.Request;
import com.example.bankcards.enums.RequestType;
import com.example.bankcards.enums.Status;
import com.example.bankcards.exception.NotCardOwnerException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.PersonRepository;
import com.example.bankcards.repository.RequestRepository;
import com.example.bankcards.service.impl.PersonServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceImplTest {
    @InjectMocks
    private PersonServiceImpl personService;

    @Mock
    private PersonRepository personRepository;
    @Mock
    private BankCardRepository bankCardRepository;
    @Mock
    private BankCardSpecification bankCardSpecification;
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private BankCardSpecificationBuilder bankCardSpecificationBuilder;

    BankCard bankCard;

    @BeforeEach
    public void setUp() {
        bankCard = BankCard.builder()
                .id(1L)
                .cardNumber("1234 5678 9012 3456")
                .label("Основная карта")
                .owner(Person.builder()
                        .id(100L)
                        .firstName("Иван")
                        .lastName("Иванов")
                        .email("ivan.ivanov@example.com")
                        .requests(new ArrayList<>())
                        .build())
                .expiryDate(LocalDateTime.now().plusYears(3))
                .status(Status.ACTIVE)
                .balance(new BigDecimal("15789.45"))
                .build();
    }
    @Test
    public void viewBalance(){
        //mock security
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("ivan.ivanov@example.com");

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        //when
        when(bankCardRepository.findById(1L)).thenReturn(Optional.of(bankCard));
        BigDecimal balance = personService.viewBalance(1L);
        //then
        Assertions.assertEquals(new BigDecimal("15789.45"), balance);
        Assertions.assertNotNull(balance);

    }

    @Test
    public void viewBalance_throwsException_ifWrongUser(){
        //given
        bankCard.setOwner(Person.builder().email("vovka@gmail.com").build());
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("ivan.ivanov@example.com");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        //when
        when(bankCardRepository.findById(1L)).thenReturn(Optional.of(bankCard));
        Assertions.assertThrows(NotCardOwnerException.class, () -> personService.viewBalance(1L));
    }

    @Test
    void viewAll_shouldReturnCardsForCurrentUser() {
        //given
        setContext();

        Person currentPerson = stubCurrentPerson("ivan.ivanov@example.com", 100L);

        FilterRequest filterRequest = new FilterRequest();
        Specification<BankCard> spec = (root, cq, cb) -> cb.conjunction();
        when(bankCardSpecificationBuilder.build(filterRequest)).thenReturn(spec);

        Pageable pageable = PageRequest.of(0, 10);
        BankCard bankCard = BankCard.builder().id(1L).owner(currentPerson).build();
        Page<BankCard> page = new PageImpl<>(List.of(bankCard));
        when(bankCardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // when
        Page<BankCardDTO> result = personService.viewAll(pageable, filterRequest);

        // then
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(currentPerson.getId(), result.getContent().get(0).getOwner().getId());
    }

    @Test
    public void sendRequestToBlockACard(){
        //given
        Request request = Request
                .builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan.ivanov@example.com")
                .build();
        //when
        personService.requestToBlockTheCard(request.getId());
        //then
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository).save(captor.capture());
    }

    @Test
    public void sendRequestToCreateACard(){
        //given
        RequestToCardCreate requestCreate = RequestToCardCreate
                .builder()
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan.ivanov@example.com")
                .build();
        //when
        personService.requestToCreateTheCard(requestCreate);

        //then
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository).save(captor.capture());
    }

    @Test
    public void methodShouldReturnAllRequests_forCurrentUser(){
        setContext();

        Person currentPerson = stubCurrentPerson("ivan.ivanov@example.com", 100L);
        Request req1 = Request.builder()
                .id(1L)
                .firstName(currentPerson.getFirstName())
                .lastName(currentPerson.getLastName())
                .email(currentPerson.getEmail())
                .person(currentPerson)
                .build();

        Request req2 = Request.builder()
                .id(2L)
                .firstName(currentPerson.getFirstName())
                .lastName(currentPerson.getLastName())
                .email(currentPerson.getEmail())
                .person(currentPerson)
                .build();
        List<Request> requests = List.of(req1, req2);
        when(requestRepository.findAllByPersonId(100L)).thenReturn(requests);

        //when
        List<RequestDTO> result = personService.getAnswers();

        //then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(currentPerson.getId(), result.get(0).getPerson().getId());
        Assertions.assertEquals(currentPerson.getFirstName(), result.get(1).getPerson().getFirstName());

        verify(requestRepository, times(1)).findAllByPersonId(100L);
    }

    @Test
    public void sendMoneyShouldTransferMoneyBetweenCards(){
        // given
        TransferRequest request = new TransferRequest();
        request.setCardNumberFrom("1111");
        request.setCardNumberTo("2222");
        request.setMoney(new BigDecimal("100.00"));

        BankCard cardFrom = BankCard.builder()
                .cardNumber("1111")
                .balance(new BigDecimal("500.00"))
                .build();

        BankCard cardTo = BankCard.builder()
                .cardNumber("2222")
                .balance(new BigDecimal("200.00"))
                .build();

        when(bankCardRepository.findByCardNumber("1111")).thenReturn(Optional.of(cardFrom));
        when(bankCardRepository.findByCardNumber("2222")).thenReturn(Optional.of(cardTo));

        //when
        String result = personService.sendMoney(request);

        //then
        Assertions.assertEquals("Операция перевода прошла успешно", result);
        Assertions.assertEquals(new BigDecimal("400.00"), cardFrom.getBalance());
        Assertions.assertEquals(new BigDecimal("300.00"), cardTo.getBalance());

        verify(bankCardRepository, times(2)).save(any(BankCard.class));
    }

    private void setContext() {
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("ivan.ivanov@example.com");

        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    private Person stubCurrentPerson(String email, Long id){
        Person person = Person.builder().id(id).email(email).firstName("Иван").lastName("Иванов").requests(new ArrayList<>()).build();
        when(personRepository.findByEmail(email)).thenReturn(Optional.of(person));
        return person;
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
