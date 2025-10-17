package com.example.bankcards.service;

import com.example.bankcards.entity.Person;
import com.example.bankcards.repository.PersonRepository;
import com.example.bankcards.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private PersonRepository personRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    Person person;

    @BeforeEach
    public void setUp() {
        person = new Person();
    }

    @Test
    public void userCreatesAndSaves_toDatabase() {
        //given
        person.setFirstName("John");
        person.setLastName("Smith");
        person.setEmail("john.smith@gmail.com");
        person.setPassword("password");

        //when

        //then

    }
}
