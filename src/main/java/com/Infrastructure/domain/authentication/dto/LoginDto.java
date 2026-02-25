package com.infrastructure.domain.authentication.dto;

import com.infrastructure.model.Users;
import com.infrastructure.validator.NotEmpty;
import com.infrastructure.validator.ValidateField;
import lombok.*;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginDto {
    @ValidateField(fieldName = "username", entityClass = Users.class)
    private String username;
    @NotEmpty(fieldName = "password")
    private String password;
}
