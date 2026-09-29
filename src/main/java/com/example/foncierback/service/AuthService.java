package com.example.foncierback.service;

import com.example.foncierback.dto.request.AcquereurRequest;
import com.example.foncierback.dto.request.LoginRequest;
import com.example.foncierback.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse registerAcquereur(AcquereurRequest request);
}