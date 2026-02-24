package com.infrastructure.domain.authentication.service;

import com.infrastructure.config.jpa.TenantContext;
import com.infrastructure.config.security.RequestContext;
import com.infrastructure.config.tokenManager.TokenManager;
import com.infrastructure.domain.authentication.dto.LoginDto;
import com.infrastructure.domain.authentication.repository.UsersRepository;
import com.infrastructure.exceptions.AuthenticationExceptionType;
import com.infrastructure.exceptions.BaseException;
import com.infrastructure.model.Users;
import com.infrastructure.util.AppUtils;
import com.infrastructure.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UsersRepository usersRepository;
    private final TokenManager tokenService;
    private final JwtUtil jwtUtil;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    public AuthenticationService(UsersRepository usersRepository,
                                 TokenManager tokenService,
                                 JwtUtil jwtUtil) {
        this.usersRepository = usersRepository;
        this.tokenService = tokenService;
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginDto loginDto) throws Exception {
        Users user = getUser(loginDto.getUsername(), loginDto.getPassword());
        return tokenService.generateToken(TenantContext.getCurrentTenant(), String.valueOf(user.getId()), user);
    }

    public String refreshToken() throws Exception {
        String token = RequestContext.getToken();
        Object user = JwtUtil.getTokenData(token);
        logout(token);
        return tokenService.generateToken(TenantContext.getCurrentTenant(), String.valueOf(((Users) user).getId()), user);
    }

    private Users getUser(String username, String password) {
        Optional<Users> user = usersRepository.findByUsernameAndPassword(username, password);
        if (!user.isPresent()) {
            throw new BaseException(AuthenticationExceptionType.USERNAME_PASSWORD_INVALID);
        }
        if (!activeProfile.equals("dev")) {
            boolean validated = AppUtils.encodePassword(password)
                    .equalsIgnoreCase(user.get().getPassword());
            if (!validated) {
                throw new BaseException(AuthenticationExceptionType.USERNAME_PASSWORD_INVALID);
            }
            if (!user.get().getIsActive()) {
                throw new BaseException(AuthenticationExceptionType.USER_IS_NOT_ACTIVE);
            }
        }
        return user.get();

    }

    public void logout(String token) throws Exception {
        if (AppUtils.isNull(token))
            throw new BaseException(AuthenticationExceptionType.TOKEN_IS_NULL);
        Object object = JwtUtil.getTokenData(token);
        if (object == null)
            throw new BaseException(AuthenticationExceptionType.TOKEN_IS_NULL);

        tokenService.revokeToken(TenantContext.getCurrentTenant(), String.valueOf(((Users) object).getId()));
    }

}
