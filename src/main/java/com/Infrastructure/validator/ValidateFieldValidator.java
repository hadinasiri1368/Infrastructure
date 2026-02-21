package com.infrastructure.validator;

import com.infrastructure.util.AppUtils;
import com.infrastructure.util.NumberUtil;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;

@Slf4j
public class ValidateFieldValidator implements ConstraintValidator<ValidateField, Object> {
    private ValidateField annotation;
    private final EntityManager entityManager;

    public ValidateFieldValidator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void initialize(ValidateField constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean valid = false;
        if (AppUtils.isNull(value)) {
            return true;
        }
        try {
            Object object = entityManager.find(annotation.entityClass(), NumberUtil.longValue(value));
            if (object != null) {
                Field field = getFieldFromClassHierarchy(annotation.entityClass(), annotation.fieldName());
                field.setAccessible(true);
                Object fieldValue = field.get(object);
                if (!AppUtils.isNull(fieldValue) && fieldValue.toString().equals(value.toString()))
                    return true;
            }
        } catch (Exception e) {
            valid = false;
        }
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(annotation.message() + "&" + annotation.fieldName()).addConstraintViolation();
            return false;
        }
        return true;
    }


    private Field getFieldFromClassHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> currentClass = clazz;

        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy.");
    }

}