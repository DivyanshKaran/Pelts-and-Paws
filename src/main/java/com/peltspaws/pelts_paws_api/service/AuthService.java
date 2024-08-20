package com.peltspaws.pelts_paws_api.service;

import com.peltspaws.pelts_paws_api.dto.auth.AuthResponse;
import com.peltspaws.pelts_paws_api.dto.auth.LoginRequest;
import com.peltspaws.pelts_paws_api.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
