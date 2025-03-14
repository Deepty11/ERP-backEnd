package com.example.ERPSpringBootBackEnd.controllers;

import com.example.ERPSpringBootBackEnd.dto.requestDto.AuthenticationRequest;
import com.example.ERPSpringBootBackEnd.dto.requestDto.AuthenticationResponse;
import com.example.ERPSpringBootBackEnd.dto.responseDto.ErrorResponseDto;
import com.example.ERPSpringBootBackEnd.exception.AuthenticationFailedException;
import com.example.ERPSpringBootBackEnd.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

@RestController
public class LoginController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) throws AuthenticationFailedException {
        String token = authenticationService.authenticate(request.getUsername(), request.getPassword());
        return Objects.isNull(token)
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDto(
                        "Invalid Username or password",
                        new Date().getTime(),
                        null))
                : ResponseEntity.ok().body(new AuthenticationResponse(token));
    }
}
