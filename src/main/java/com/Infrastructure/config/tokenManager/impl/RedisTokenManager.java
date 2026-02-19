package com.infrastructure.config.tokenManager.impl;

import com.infrastructure.Users;
import com.infrastructure.config.tokenManager.TokenManager;
import com.infrastructure.exceptions.AuthenticationExceptionType;
import com.infrastructure.exceptions.BaseException;
import com.infrastructure.exceptions.GeneralExceptionType;
import com.infrastructure.util.AppUtils;
import com.infrastructure.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RedisTokenManager implements TokenManager {

    private final RedisTemplate<String, String> redisTemplate;
    private final DefaultRedisScript<String> generateTokenScript;
    private final long tokenExpirySeconds;

    public RedisTokenManager(
            RedisTemplate<String, String> redisTemplate,
            @Value("${jwt.expirationMinutes}") long tokenExpiryMinutes) {

        this.redisTemplate = redisTemplate;
        this.tokenExpirySeconds = tokenExpiryMinutes * 60;

        this.generateTokenScript = new DefaultRedisScript<>();
        this.generateTokenScript.setScriptText(GENERATE_TOKEN_SCRIPT);
        this.generateTokenScript.setResultType(String.class);
    }

    private static final String GENERATE_TOKEN_SCRIPT = """
        local existing = redis.call('GET', KEYS[1])
        if existing then
            return existing
        else
            redis.call('SET', KEYS[1], ARGV[1], 'EX', ARGV[2])
            return ARGV[1]
        end
    """;

    private String sessionKey(String tenantId, String userId) {
        return "session_token:" + tenantId + ":" + userId;
    }

    @Override
    public String generateToken(String tenantId, String userId, Object user) {

        validateIds(tenantId, userId);

        String key = sessionKey(tenantId, userId);

        String newToken = JwtUtil.createToken(user);

        return redisTemplate.execute(
                generateTokenScript,
                Collections.singletonList(key),
                newToken,
                String.valueOf(tokenExpirySeconds)
        );
    }

    @Override
    public boolean isTokenValid(String token, String tenantId, String userId) {

        if (!JwtUtil.validateToken(token)) {
            return false;
        }

        String storedToken = redisTemplate.opsForValue().get(sessionKey(tenantId, userId));

        return !AppUtils.isNull(storedToken);
    }

    @Override
    public void revokeToken(String tenantId, String userId) {

        validateIds(tenantId, userId);

        redisTemplate.delete(sessionKey(tenantId, userId));
    }

    @Override
    public Object getTokenData(String tenantId, String userId, String token) {

        if (!isTokenValid(token, tenantId, userId)) {
            throw new BaseException(AuthenticationExceptionType.TOKEN_IS_NULL);
        }

        return JwtUtil.getTokenData(token);
    }

    @Override
    public String refreshToken(String oldToken) {
        throw new UnsupportedOperationException("Refresh not implemented");
    }

    private void validateIds(String tenantId, String userId) {

        if (tenantId == null || tenantId.isBlank())
            throw new BaseException(GeneralExceptionType.SCHEMAID_ID_IS_NULL);

        if (userId == null || userId.isBlank())
            throw new BaseException(GeneralExceptionType.FIELD_NOT_VALID, new Object[]{"userId"});
    }
}
