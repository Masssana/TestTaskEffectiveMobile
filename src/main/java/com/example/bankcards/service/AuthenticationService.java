package com.example.bankcards.service;

import com.example.bankcards.dto.AuthenticationRequest;
import com.example.bankcards.dto.AuthenticationResponse;
import com.example.bankcards.dto.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest registerRequest);
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
