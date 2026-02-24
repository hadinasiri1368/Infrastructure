package com.infrastructure.config.tokenManager;

public interface TokenManager {

    String generateToken(String tenantId, String userId, Object user);

    boolean isTokenValid(String token, String tenantId, String userId);

    void revokeToken(String tenantId, String userId);

    Object getTokenData(String tenantId, String userId, String token);

    String refreshToken(String oldToken);
}

