package com.library.library_management.controller;

import com.library.library_management.dto.auth.AuthenticationRequest;
import com.library.library_management.dto.auth.RegisterRequest;
import com.library.library_management.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public String register(
            @Valid @RequestBody RegisterRequest request
    ) {

        log.info(
                "Registration request received for username: {}",
                request.getUsername()
        );

        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public String login(
            @Valid @RequestBody AuthenticationRequest request
    ) {

        log.info(
                "Login request received for username: {}",
                request.getUsername()
        );

        return authenticationService.login(request);
    }
}