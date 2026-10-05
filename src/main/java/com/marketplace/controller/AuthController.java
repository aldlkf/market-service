package com.marketplace.controller;

import com.marketplace.dto.AuthRequest;
import com.marketplace.dto.AuthResponse;
import com.marketplace.model.Role;
import com.marketplace.model.User;
import com.marketplace.repository.UserRepository;
import com.marketplace.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Optional;


@RestController
@RequestMapping("api/auth")
public class AuthController {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
            this.jwtService = jwtService;
        }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = new User();
        user.setName("New User");
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.ZERO);

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request){
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(passwordEncoder.matches(request.getPassword(), user.getPassword())){
                String token = jwtService.generateToken(user.getEmail());
                return ResponseEntity.ok(new AuthResponse(token));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
}