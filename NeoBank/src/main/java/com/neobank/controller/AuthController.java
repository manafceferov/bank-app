package com.neobank.controller;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.auth.LoginRequestDto;
import com.neobank.dto.auth.LoginResponseDto;
import com.neobank.dto.auth.RegisterRequestDto;
import com.neobank.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequestDto dto) {
        return authService.register(dto);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        return authService.login(dto);
    }
}