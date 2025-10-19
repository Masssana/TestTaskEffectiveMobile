package com.example.bankcards.service;

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
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.PersonRepository;
import com.example.bankcards.repository.RequestRepository;
import com.example.bankcards.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {
    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private BankCardRepository bankCardRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    BankCard bankCard;
    Person person;

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

        person = (Person.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan.ivanov@example.com")
                .requests(new ArrayList<>())
                .password("123")
                .build());
    }
    @Test
    public void getAllBankCards_shouldReturnAllBankCards() {
        BankCard bankCard2 = BankCard.builder()
                .id(1L)
                .cardNumber("5831 5678 9912 3456")
                .label("Важная карта")
                .owner(Person.builder()
                        .id(100L)
                        .firstName("Гриша")
                        .lastName("Иванов")
                        .email("vova.vovo@example.com")
                        .requests(new ArrayList<>())
                        .build())
                .expiryDate(LocalDateTime.now().plusYears(3))
                .status(Status.ACTIVE)
                .balance(new BigDecimal("15789.45"))
                .build();
        List<BankCard> bankCards = List.of(bankCard, bankCard2);
        when(bankCardRepository.findAll()).thenReturn(bankCards);

        adminService.getAllBankCards();

        Assertions.assertEquals(2, bankCards.size());
        Assertions.assertEquals(bankCard, bankCards.get(0));
        Assertions.assertEquals(bankCard2, bankCards.get(1));
        verify(bankCardRepository, times(1)).findAll();
    }

    @Test
    public void getCardByNumber_shouldReturnCardByNumber() {
        when(bankCardRepository.findById(1L)).thenReturn(Optional.of(bankCard));

        BankCardDTO result = adminService.getCardByNumber(1L);

        verify(bankCardRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(bankCardRepository);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("**** **** **** 3456", result.getCardNumber());
        Assertions.assertEquals("Основная карта", result.getLabel());
    }

    @Test
    public void getCardByNumber_shouldThrowExceptionWhenCardNotFound() {
        bankCard.setId(4L);
        when(bankCardRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BankCardNotFoundException.class, () -> adminService.getCardByNumber(1L));

        verify(bankCardRepository, times(1)).findById(1L);
    }

    @Test
    public void admin_shouldBeAbleToCreateBankCard() {
        BankCardDTO bankCardDTO = BankCardDTO.builder()
                .cardNumber("5831 5678 9912 3456")
                .label("Важная карта")
                .owner(PersonDTO.from(Person.builder()
                        .id(100L)
                        .firstName("Гриша")
                        .lastName("Иванов")
                        .email("vova.vovo@example.com")
                        .requests(new ArrayList<>())
                        .build()))
                .expiryDate(LocalDateTime.now().plusYears(3))
                .status(Status.ACTIVE)
                .balance(new BigDecimal("15789.45"))
                .build();

        adminService.createBankCard(bankCardDTO);

        ArgumentCaptor<BankCard> captor = ArgumentCaptor.forClass(BankCard.class);
        verify(bankCardRepository, times(1)).save(captor.capture());
        BankCard result = captor.getValue();

        Assertions.assertEquals(bankCardDTO.getCardNumber(), result.getCardNumber());
        Assertions.assertEquals(bankCardDTO.getLabel(), result.getLabel());
        Assertions.assertEquals(bankCardDTO.getStatus(), result.getStatus());
        Assertions.assertEquals(bankCardDTO.getBalance(), result.getBalance());
        Assertions.assertEquals(bankCardDTO.getOwner().getId(), result.getOwner().getId());
    }

    @Test
    public void adminShouldBeAbleToBlockCard(){
        when(bankCardRepository.findById(1L)).thenReturn(Optional.of(bankCard));

        adminService.blockCard(1L);

        verify(bankCardRepository, times(1)).findById(1L);
        Assertions.assertNotNull(bankCard);
        Assertions.assertEquals(Status.BLOCKED, bankCard.getStatus());
    }

    @Test
    public void exception_shouldBeThrownWhenCardNotFound() {
        when(bankCardRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BankCardNotFoundException.class, () -> adminService.blockCard(1L));

        verify(bankCardRepository, times(1)).findById(1L);
    }

    @Test
    public void admin_shouldBeAbleToActivateBankCard() {
        when(bankCardRepository.findById(1L)).thenReturn(Optional.of(bankCard));

        adminService.activateCard(1L);

        verify(bankCardRepository, times(1)).findById(1L);
        Assertions.assertNotNull(bankCard);
        Assertions.assertEquals(Status.ACTIVE, bankCard.getStatus());
    }

    @Test
    public void active_shouldThrowExceptionWhenCardNotFound() {
        when(bankCardRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BankCardNotFoundException.class, () -> adminService.activateCard(1L));

        verify(bankCardRepository, times(1)).findById(1L);
    }

    @Test
    public void adminShouldBeAbleToDeleteBankCard() {
        when(bankCardRepository.findById(1L)).thenReturn(Optional.of(bankCard));

        adminService.deleteCard(1L);

        verify(bankCardRepository, times(1)).findById(1L);
        verify(bankCardRepository, times(1)).delete(bankCard);
        verifyNoMoreInteractions(bankCardRepository);

    }

    @Test
    public void deleteCard_shouldThrowExceptionWhenCardNotFound() {
        when(bankCardRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BankCardNotFoundException.class, () -> adminService.deleteCard(1L));

        verify(bankCardRepository, times(1)).findById(1L);
    }

    @Test
    public void adminShouldBeAbleToMakeUserAdmin(){
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        adminService.makeUserAdmin(1L);

        verify(personRepository, times(1)).findById(1L);
        verify(personRepository, times(1)).findById(1L);
        Assertions.assertNotNull(person);
        Assertions.assertEquals(Role.ADMIN, person.getRole());
    }

    @Test
    public void makeAdminShouldThrowExceptionWhenUserNotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> adminService.makeUserAdmin(1L));

        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    public void adminShouldBeAbleToMakeAdminUser(){
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        adminService.makeAdminUser(1L);

        verify(personRepository, times(1)).findById(1L);
        verify(personRepository, times(1)).findById(1L);
        Assertions.assertNotNull(person);
        Assertions.assertEquals(Role.USER, person.getRole());
    }

    @Test
    public void makeUserShouldThrowExceptionWhenUserNotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> adminService.makeAdminUser(1L));

        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    public void adminShouldBeAbleToSeeAllUsers(){
       Person person2 = (Person.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan.ivanov@example.com")
                .requests(new ArrayList<>())
                .build());
       List<Person> persons = List.of(person, person2);

       when(personRepository.findAll()).thenReturn(persons);

       adminService.getAllUsers();

       verify(personRepository, times(1)).findAll();
       Assertions.assertEquals(2, persons.size());
       Assertions.assertEquals(person, persons.get(0));
       Assertions.assertEquals(person2, persons.get(1));
    }

    @Test
    public void adminShouldBeAbleToDeleteUser(){
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        adminService.deleteUser(1L);

        verify(personRepository, times(1)).findById(1L);
        verify(personRepository, times(1)).delete(person);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void adminShouldBeAbleToCreateUser(){
        PersonCreateRequest personCreateRequest = PersonCreateRequest
                .builder()
                .firstName("Vova")
                .lastName("Vovov")
                .email("vova@gmail.com")
                .role(Role.USER)
                .password("123")
                .build();

        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        adminService.createUser(personCreateRequest);

        ArgumentCaptor<Person> personArgumentCaptor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository, times(1)).save(personArgumentCaptor.capture());
        Person person = personArgumentCaptor.getValue();

        Assertions.assertNotNull(person);
        Assertions.assertEquals(Role.USER, person.getRole());
        Assertions.assertEquals(personCreateRequest.getFirstName(), person.getFirstName());
        Assertions.assertEquals(personCreateRequest.getLastName(), person.getLastName());
        Assertions.assertEquals(personCreateRequest.getEmail(), person.getEmail());
        Assertions.assertEquals("encoded123", person.getPassword());
    }

    @Test
    public void acceptTheRequest_shouldUpdateRequest() {
        Request request = Request.builder()
                .id(1L)
                .requestType(null)
                .requestAnswer(null)
                .build();
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        adminService.acceptTheRequest(1L);

        verify(requestRepository, times(1)).findById(1L);
        verify(requestRepository, times(1)).save(request);

        Assertions.assertEquals(RequestType.CREATE, request.getRequestType());
        Assertions.assertEquals(RequestAnswer.ACCEPTED, request.getRequestAnswer());
    }

    @Test
    public void rejectTheRequest_shouldUpdateRequest() {
        Request request = Request.builder()
                .id(2L)
                .requestType(null)
                .requestAnswer(null)
                .build();
        when(requestRepository.findById(2L)).thenReturn(Optional.of(request));

        adminService.rejectTheRequest(2L);

        verify(requestRepository, times(1)).findById(2L);
        verify(requestRepository, times(1)).save(request);

        Assertions.assertEquals(RequestType.BLOCk, request.getRequestType());
        Assertions.assertEquals(RequestAnswer.DECLINED, request.getRequestAnswer());
    }

    @Test
    public void getAllRequests_shouldReturnAllRequests() {
        Request req1 = Request.builder().id(1L).person(person).build();
        Request req2 = Request.builder().id(2L).person(person).build();
        List<Request> requests = List.of(req1, req2);

        when(requestRepository.findAll()).thenReturn(requests);

        List<RequestDTO> result = adminService.getAllRequests();

        verify(requestRepository, times(1)).findAll();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(req1.getId(), result.get(0).getId());
        Assertions.assertEquals(req2.getId(), result.get(1).getId());
    }
}
