package com.angular_training.demo.controller;

import com.angular_training.demo.security.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.angular_training.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import com.angular_training.demo.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static com.angular_training.demo.model.User.Role.ROLE_ADMIN;
import static com.angular_training.demo.model.User.Role.ROLE_USER;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authManager,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("Username đã tồn tại");
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(ROLE_USER)
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User req) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()
                );

        authManager.authenticate(authToken); // sai sẽ ném exception

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(); // chắc chắn tồn tại vì authenticate đã pass

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of("accessToken", token));
    }


}
