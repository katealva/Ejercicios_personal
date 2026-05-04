package com.example.oreo2.controller;

import com.example.oreo2.common.NotFoundException;
import com.example.oreo2.dto.UserResponse;
import com.example.oreo2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CENTRAL')")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public List<UserResponse> listAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        if (!userRepository.existsById(id))
            throw new NotFoundException("User not found: " + id);
        userRepository.deleteById(id);
    }
}
