package com.infrastructure.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@Slf4j
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final Object[] params;

    public BaseException(GeneralExceptionType generalExceptionType) {
        super(generalExceptionType.getMessageKey());
        this.status = generalExceptionType.getHttpStatus();
        this.params = null;
    }

    public BaseException(GeneralExceptionType generalExceptionType, Object[] params) {
        super(generalExceptionType.getMessageKey());
        this.status = generalExceptionType.getHttpStatus();
        this.params = params;
    }

    public BaseException(AuthenticationExceptionType authenticationExceptionType) {
        super(authenticationExceptionType.getMessageKey());
        this.status = authenticationExceptionType.getHttpStatus();
        this.params = null;
    }

    public BaseException(AuthenticationExceptionType authenticationExceptionType, Object[] params) {
        super(authenticationExceptionType.getMessageKey());
        this.status = authenticationExceptionType.getHttpStatus();
        this.params = params;
    }
}
