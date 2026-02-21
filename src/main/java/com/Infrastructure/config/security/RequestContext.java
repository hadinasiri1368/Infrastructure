package com.infrastructure.config.security;

import com.infrastructure.Users;
import com.infrastructure.util.AppUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class RequestContext {
    private static final ThreadLocal<String> uuid = new ThreadLocal<>();

    public static String getUuid() {
        String currentUuid = uuid.get();
        if (currentUuid == null) {
            currentUuid = java.util.UUID.randomUUID().toString();
            uuid.set(currentUuid);
        }
        return currentUuid;
    }

    public static Users getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            CustomUserDetails userDetail = ((CustomUserDetails) auth.getPrincipal());
            if (userDetail == null)
                return null;
            return userDetail.getUser();
        }
        return null;
    }

    public static Long getUserId() {
        Users currentUser = getUser();
        return AppUtils.isNull(currentUser) ? null : currentUser.getId();
    }

    public static String getToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return ((String) auth.getCredentials());
        }
        return null;
    }

    public static void clear() {
        uuid.remove();
    }
}

