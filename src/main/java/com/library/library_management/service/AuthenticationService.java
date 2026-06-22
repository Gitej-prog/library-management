package com.library.library_management.service;

import com.library.library_management.dto.auth.AuthenticationRequest;
import com.library.library_management.dto.auth.RegisterRequest;
import com.library.library_management.entity.Role;
import com.library.library_management.entity.User;
import com.library.library_management.repository.RoleRepository;
import com.library.library_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {

        log.info(
                "Registering user: {} with role: {}",
                request.getUsername(),
                request.getRoleName()
        );

        Role role = roleRepository.findByName(
                request.getRoleName()
        ).orElseThrow(() -> {

            log.error(
                    "Role not found: {}",
                    request.getRoleName()
            );

            return new RuntimeException(
                    "Role not found"
            );
        });

        log.info(
                "Role found: {}",
                role.getName()
        );

        User user = User.builder()
                .username(request.getUsername())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(role)
                .build();

        User savedUser = userRepository.save(user);

        log.info(
                "User registered successfully with id: {} and username: {}",
                savedUser.getId(),
                savedUser.getUsername()
        );

        return "User Registered Successfully";
    }

    public String login(AuthenticationRequest request) {

        log.info(
                "Login attempt for username: {}",
                request.getUsername()
        );

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        log.info(
                "Login successful for username: {}",
                request.getUsername()
        );

        return "Login Successful";
    }
}