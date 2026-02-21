package com.infrastructure.validator;


import com.infrastructure.constants.Consts;
import com.infrastructure.util.AppUtils;
import com.infrastructure.util.DateUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PersianDateValidator implements ConstraintValidator<ValidPersianDate, String> {
    private static final Pattern PATTERN = Pattern.compile(Consts.PERSIAN_DATE_REGEX);
    private ValidPersianDate annotation;


    @Override
    public void initialize(ValidPersianDate constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = true;
        if (AppUtils.isNull(value)) {
            return true;
        }
        if (!PATTERN.matcher(value).matches()) {
            isValid = false;
        } else if (!DateUtil.isValid(value)) {
            isValid = false;
        }
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(annotation.message() + "&" + annotation.fieldName()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
