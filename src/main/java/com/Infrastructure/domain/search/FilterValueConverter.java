package com.infrastructure.domain.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilterValueConverter {


    public List<FilterField> convertFilterValues(List<FilterField> filters, Class<?> entityClass) {
        return filters.stream()
                .map(filter -> convertSingleFilter(filter, entityClass))
                .collect(Collectors.toList());
    }

    private FilterField convertSingleFilter(FilterField filter, Class<?> entityClass) {
        try {
            Class<?> fieldType = getFieldType(entityClass, filter.getField());

            Object convertedValue = convertValue(filter.getValue(), fieldType);

            Object convertedValueTo = null;
            if (filter.getValueTo() != null) {
                convertedValueTo = convertValue(filter.getValueTo(), fieldType);
            }

            return FilterField.builder()
                    .field(filter.getField())
                    .op(filter.getOp())
                    .value(convertedValue)
                    .valueTo(convertedValueTo)
                    .build();

        } catch (Exception e) {
            log.error("Error converting filter: {}", filter, e);
            throw new RuntimeException("Cannot convert filter value for field: " + filter.getField());
        }
    }

    private Class<?> getFieldType(Class<?> entityClass, String fieldPath) {
        try {
            if (!fieldPath.contains(".")) {
                Field field = entityClass.getDeclaredField(fieldPath);
                return field.getType();
            } else {
                String[] parts = fieldPath.split("\\.");
                Class<?> currentClass = entityClass;

                for (int i = 0; i < parts.length - 1; i++) {
                    Field field = currentClass.getDeclaredField(parts[i]);
                    currentClass = field.getType();
                }

                Field lastField = currentClass.getDeclaredField(parts[parts.length - 1]);
                return lastField.getType();
            }
        } catch (NoSuchFieldException e) {
            log.warn("Field not found: {}", fieldPath);
            return String.class;
        }
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        String stringValue = value.toString();

        if (targetType.equals(String.class)) {
            return stringValue;
        }

        if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return Integer.valueOf(stringValue);
        }

        if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return Long.valueOf(stringValue);
        }

        if (targetType.equals(Double.class) || targetType.equals(double.class)) {
            return Double.valueOf(stringValue);
        }

        if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            return Boolean.valueOf(stringValue);
        }

        if (targetType.equals(LocalDate.class)) {
            return LocalDate.parse(stringValue);
        }

        if (targetType.equals(LocalDateTime.class)) {
            return LocalDateTime.parse(stringValue);
        }

        if (targetType.isEnum()) {
            try {
                return Enum.valueOf((Class<Enum>) targetType, stringValue);
            } catch (IllegalArgumentException e) {
                return Enum.valueOf((Class<Enum>) targetType, stringValue.toUpperCase());
            }
        }

        return value;
    }
}
