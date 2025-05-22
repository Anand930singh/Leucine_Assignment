package com.example.todo.controller;

import com.example.todo.dto.LoginDto;
import com.example.todo.dto.RegisterDto;
import com.example.todo.dto.ResponseDto;
import com.example.todo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto<Object>> register(@RequestBody RegisterDto registerDto) {
        System.out.println("Helokhsdf jhdsgfh ");
        ResponseDto<Object> response = authService.registerUser(registerDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Object>> login(@RequestBody LoginDto loginDto) {
        ResponseDto<Object> response = authService.loginUser(loginDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
