package com.infrastructure.validator;

import com.infrastructure.util.AppUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NationalCodeValidator implements ConstraintValidator<ValidNationalCode, String> {
    private ValidNationalCode annotation;

    @Override
    public void initialize(ValidNationalCode constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext context) {
        if(!AppUtils.checkNationalCode(string)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(annotation.message()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
