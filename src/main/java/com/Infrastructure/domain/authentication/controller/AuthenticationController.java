package com.infrastructure.domain.authentication.controller;

import com.infrastructure.domain.authentication.dto.LoginDto;
import com.infrastructure.domain.authentication.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping(path = "/login")
    public String login(@RequestBody LoginDto loginDto) throws Exception {
        return service.login(loginDto);
    }

    @PutMapping(path = "/refreshToken")
    public String refreshToken() throws Exception {
        return service.refreshToken();
    }
}
