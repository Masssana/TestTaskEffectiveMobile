package com.example.bankcards.service;

import com.example.bankcards.dto.AuthenticationRequest;
import com.example.bankcards.dto.AuthenticationResponse;
import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.entity.Person;
import com.example.bankcards.enums.Role;
import com.example.bankcards.repository.PersonRepository;
import com.example.bankcards.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Mock
    private AuthenticationManager authenticationManager;

    RegisterRequest registerRequest;
    AuthenticationRequest authenticationRequest;

    @BeforeEach
    public void setUp() {
        registerRequest = new RegisterRequest();
        authenticationRequest = new AuthenticationRequest();
    }

    @Test
    public void register_savesUserToDatabase_and_returnResponseWithToken() {
        //given
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Smith");
        registerRequest.setEmail("john.smith@gmail.com");
        registerRequest.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("password");
        when(jwtService.generateToken(any(Person.class))).thenReturn("token");

        //when
        AuthenticationResponse response = authenticationService.register(registerRequest);
        //then
        ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository).save(personCaptor.capture());
        Person person = personCaptor.getValue();

        Assertions.assertEquals("John", person.getFirstName());
        Assertions.assertEquals("Smith", person.getLastName());
        Assertions.assertEquals("john.smith@gmail.com", person.getEmail());
        Assertions.assertEquals("password", person.getPassword());

        Assertions.assertEquals("token", response.getToken());
        Assertions.assertNotNull(response);
    }

    @Test
    public void authenticate_method_doAuthenticate_and_returnToken(){
        //given
        authenticationRequest.setEmail("john.smith@gmail.com");
        authenticationRequest.setPassword("password");

        Person user = Person.builder()
                .id(1L)
                .email("john.smith@gmail.com")
                .password("password")
                .role(Role.USER)
                .build();

        when(personRepository.findByEmail("john.smith@gmail.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(Person.class))).thenReturn("token");

        Authentication fakeAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(fakeAuth);
        //when
        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        //then
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(personRepository).findByEmail("john.smith@gmail.com");
        verify(jwtService).generateToken((user));

        Assertions.assertEquals("token", response.getToken());
        Assertions.assertNotNull(response);
    }

}
