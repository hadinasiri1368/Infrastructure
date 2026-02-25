package com.infrastructure.domain.authentication.controller;

import com.infrastructure.constants.Consts;
import com.infrastructure.domain.authentication.dto.LoginDto;
import com.infrastructure.domain.authentication.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping(path = "/login")
    public String login(@RequestBody LoginDto loginDto) throws Exception {
        return service.login(loginDto);
    }

    @PutMapping(path = Consts.DEFAULT_PREFIX_API_URL + "/refreshToken")
    public String refreshToken() throws Exception {
        return service.refreshToken();
    }
}
