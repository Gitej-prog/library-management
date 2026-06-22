package com.library.library_management.security;

import com.library.library_management.entity.User;
import com.library.library_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {

        log.info(
                "Authenticating user: {}",
                username
        );

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() -> {

                            log.warn(
                                    "User not found: {}",
                                    username
                            );

                            return new UsernameNotFoundException(
                                    "User not found"
                            );
                        });

        log.info(
                "User found: {}, Role: {}",
                user.getUsername(),
                user.getRole().getName()
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().getName()
                        )
                )
        );
    }
}