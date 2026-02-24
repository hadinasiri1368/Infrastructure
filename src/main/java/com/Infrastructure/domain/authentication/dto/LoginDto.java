package com.infrastructure.domain.authentication.dto;

import com.infrastructure.model.Users;
import com.infrastructure.validator.NotEmpty;
import com.infrastructure.validator.ValidateField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Getter
@Builder
public class LoginDto {
    @ValidateField(fieldName = "username", entityClass = Users.class)
    private String username;
    @NotEmpty(fieldName = "password")
    private String password;
}
