package com.infrastructure.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GeneralExceptionType {
    ID_IS_NULL(HttpStatus.UNPROCESSABLE_ENTITY, "general_exception.id_is_null"),
    ENTITY_NOT_FOUND(HttpStatus.UNPROCESSABLE_ENTITY, "general_exception.entity_cannot_be_null"),
    DATE_CANNOT_BE_NULL(HttpStatus.BAD_REQUEST, "general_exception.date_cannot_be_null"),
    ENTITY_CANNOT_BE_NULL(HttpStatus.UNPROCESSABLE_ENTITY, "general_exception.entity_cannot_be_null"),
    DATE_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "validation.PersianDateNotValid.message"),
    FIELD_NOT_VALID(HttpStatus.INTERNAL_SERVER_ERROR, "validation.FieldNotValid.message"),
    SCHEMAID_ID_IS_NULL(HttpStatus.BAD_REQUEST, "general_exception.schemaId_id_is_null"),
    UNKNOWN_ERROR(HttpStatus.BAD_REQUEST, "general_exception.Unknown_error"),
    ;

    private final HttpStatus httpStatus;
    private final String messageKey;

    GeneralExceptionType(HttpStatus httpStatus, String messageKey) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }
}
