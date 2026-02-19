package com.infrastructure.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthenticationExceptionType {
    TOKEN_IS_NULL(HttpStatus.UNAUTHORIZED, "authorization.token_is_null"),
    USER_HAS_TOKEN(HttpStatus.CONFLICT, "authorization.user_has_token"),
    USER_HAS_NOT_TOKEN(HttpStatus.NOT_FOUND, "authorization.user_has_not_token"),
    USER_IS_NOT_ACTIVE(HttpStatus.FORBIDDEN, "authorization.user_is_not_active"),
    USERNAME_PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, "authorization.username_password_invalid"),
    VERIFY_CODE_IS_NOT_VALID(HttpStatus.BAD_REQUEST, "authorization.verify_code_is_not_valid"),
    VERIFY_CODE_TIMEOUT(HttpStatus.INTERNAL_SERVER_ERROR, "authorization.verify_code_timeout"),
    VERIFY_CODE_ATTEMPT_EXCEED(HttpStatus.INTERNAL_SERVER_ERROR, "authorization.verify_code_attempt_exceed"),
    PHYSICAL_OTP_NOT_FOUND(HttpStatus.NOT_FOUND, "authorization.physical_otp_not_found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "authorization.appuser_not_found"),
    PHYSICAL_OTP_IS_DISABLED(HttpStatus.INTERNAL_SERVER_ERROR, "authorization.physical_otp_is_disabled"),
    ALL_OTP_STRATEGY_ARE_DISABLED(HttpStatus.INTERNAL_SERVER_ERROR, "authorization.all_otp_strategy_are_disabled"),
    OTP_STRATEGY_IS_DISABLED(HttpStatus.INTERNAL_SERVER_ERROR, "authorization.otp_strategy_is_disabled"),
    USER_MOBILE_IS_NOT_VALID(HttpStatus.INTERNAL_SERVER_ERROR, "authorization.user_mobile_is_not_valid"),
    OTP_SEND_NOTALLOWED(HttpStatus.FORBIDDEN, "authorization.otp_send_notAllowed"),
    DO_NOT_HAVE_ACCESS_TO_ADDRESS(HttpStatus.FORBIDDEN, "authorization.do_not_have_access_to_address"),
    EMAIL_IS_NOT_VALID(HttpStatus.INTERNAL_SERVER_ERROR, "authorization.email_is_not_valid"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "authorization.authentication_failed"),
    URL_NOT_FOUND(HttpStatus.NOT_FOUND, "authorization.url_not_found")
    ;
    private final HttpStatus httpStatus;
    private final String messageKey;

    AuthenticationExceptionType(HttpStatus httpStatus, String messageKey) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }
}
