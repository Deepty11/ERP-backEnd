package com.example.ERPSpringBootBackEnd.controllers;

import com.example.ERPSpringBootBackEnd.dto.requestDto.AuthenticationRequest;
import com.example.ERPSpringBootBackEnd.dto.requestDto.AuthenticationResponse;
import com.example.ERPSpringBootBackEnd.dto.responseDto.ErrorResponseDto;
import com.example.ERPSpringBootBackEnd.exception.AuthenticationFailedException;
import com.example.ERPSpringBootBackEnd.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login Controller", description = "Manages Login operations")
public class LoginController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary ="Login", description = "Operates login with authentication requests")
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
