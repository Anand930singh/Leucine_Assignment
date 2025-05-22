package com.example.todo.service;

import com.example.todo.config.PasswordEncoderConfig;
import com.example.todo.dto.LoginDto;
import com.example.todo.dto.RegisterDto;
import com.example.todo.dto.ResponseDto;
import com.example.todo.entity.Users;
import com.example.todo.repository.UserRepository;
import com.example.todo.utils.JWTUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JWTUtils jwtutils;
    private final PasswordEncoderConfig passwordEncoderConfig;

    public AuthService(UserRepository userRepository, JWTUtils jwtutils, PasswordEncoderConfig passwordEncoderConfig) {
        this.userRepository = userRepository;
        this.jwtutils = jwtutils;
        this.passwordEncoderConfig = passwordEncoderConfig;
    }

    public ResponseDto<Object> registerUser(RegisterDto registerDto) {
        try {
            if (registerDto.getUsername() == null || registerDto.getEmail() == null || registerDto.getPassword() == null) {
                return new ResponseDto<>(400, "All fields are required!");
            }

            if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
                return new ResponseDto<>(409, "Username is already taken!");
            }

            if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
                return new ResponseDto<>(409, "Email is already taken!");
            }

            Users user = new Users();
            user.setUsername(registerDto.getUsername());
            user.setEmail(registerDto.getEmail());
            user.setPassword(passwordEncoderConfig.passwordEncoder().encode(registerDto.getPassword()));

            userRepository.save(user);
            return new ResponseDto<>(201, "User registered successfully!");
        } catch (Exception e) {
            return new ResponseDto<>(500, "Something went wrong: " + e.getMessage());
        }
    }

    public ResponseDto<Object> loginUser(LoginDto loginDto) {
        try {
            if (loginDto.getUserNameOrEmail() == null || loginDto.getPassword() == null) {
                return new ResponseDto<>(400, "Username/email and password are required!");
            }

            Optional<Users> userOptional = userRepository.findByUsername(loginDto.getUserNameOrEmail());
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findByEmail(loginDto.getUserNameOrEmail());
            }

            if (userOptional.isEmpty()) {
                return new ResponseDto<>(404, "User not found");
            }

            Users user = userOptional.get();

            if (!passwordEncoderConfig.passwordEncoder().matches(loginDto.getPassword(), user.getPassword())) {
                return new ResponseDto<>(401, "Incorrect password");
            }

            String jwtToken = jwtutils.generateToken(user.getUsername());
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", jwtToken);

            return new ResponseDto<>(200, "Login successful", tokenMap);
        } catch (Exception e) {
            return new ResponseDto<>(500, "Something went wrong: " + e.getMessage());
        }
    }
}
