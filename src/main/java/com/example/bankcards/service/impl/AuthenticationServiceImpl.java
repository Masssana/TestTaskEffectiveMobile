package com.example.bankcards.service.impl;

import com.example.bankcards.dto.AuthenticationRequest;
import com.example.bankcards.dto.AuthenticationResponse;
import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.entity.Person;
import com.example.bankcards.enums.Role;
import com.example.bankcards.repository.PersonRepository;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var person = Person.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
        personRepository.save(person);
        var jwtToken = jwtService.generateToken(person);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));
        var user = personRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(token).build();
    }
}
