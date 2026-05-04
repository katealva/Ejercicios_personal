package com.example.oreo2.service;

import com.example.oreo2.common.ConflictException;
import com.example.oreo2.dto.LoginRequest;
import com.example.oreo2.dto.LoginResponse;
import com.example.oreo2.dto.RegisterRequest;
import com.example.oreo2.dto.UserResponse;
import com.example.oreo2.entity.Role;
import com.example.oreo2.entity.User;
import com.example.oreo2.repository.UserRepository;
import com.example.oreo2.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserResponse register(RegisterRequest req) {
        if (req.role() == Role.BRANCH) {
            if (req.branch() == null || req.branch().isBlank())
                throw new IllegalArgumentException("branch is required when role is BRANCH");
        } else {
            if (req.branch() != null && !req.branch().isBlank())
                throw new IllegalArgumentException("branch must not be set when role is CENTRAL");
        }

        if (userRepository.existsByUsername(req.username()))
            throw new ConflictException("Username already taken: " + req.username());
        if (userRepository.existsByEmail(req.email()))
            throw new ConflictException("Email already registered: " + req.email());

        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .branch(req.role() == Role.BRANCH ? req.branch() : null)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        User user = userRepository.findByUsername(req.username()).orElseThrow();
        return new LoginResponse(jwtService.generateToken(user), 3600L,
                user.getRole(), user.getBranch());
    }
}
