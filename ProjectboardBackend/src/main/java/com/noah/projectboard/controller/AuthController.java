package com.noah.projectboard.controller;

import com.noah.projectboard.model.User;
import com.noah.projectboard.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register endpoint
    @PostMapping("/register")
    public String register(@RequestBody User registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()) != null) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);
        return "Registered successfully";
    }

    // Login endpoint
    @PostMapping("/login")
    public String login(@RequestBody User loginRequest, HttpSession session) {
        User user = userRepository.findByUsername(loginRequest.getUsername());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            session.setAttribute("user", user);
            return "Login successful";
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

    // Logout endpoint
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out";
    }

    // Get current user
    @GetMapping("/me")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}