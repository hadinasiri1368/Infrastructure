package com.infrastructure.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@Component
public class AppUtils {
    private static MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        AppUtils.messageSource = messageSource;
    }

    public static boolean executeQuery(DataSource dataSource, String query) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getToken(HttpServletRequest request) {
        if (isNull(request.getHeader("Authorization")))
            return null;
        return request.getHeader("Authorization").replaceAll("Bearer ", "");
    }

    public static boolean isNull(Object o) {
        if (o instanceof String) {
            if (o == null ||
                    ((String) o).isEmpty() ||
                    ((String) o).isBlank() ||
                    ((String) o).length() == 0 ||
                    ((String) o).toLowerCase().trim().equals("null")
            )
                return true;
            return false;
        } else if (o instanceof List) {
            if (((List) o).isEmpty())
                return true;
        }
        return o == null ? true : false;
    }

    public static String getMessageFromMessageSource(String key) {
        return getMessageFromMessageSource(key, null);
    }

    public static String getMessageFromMessageSource(String key, Object... params) {
        return messageSource.getMessage(key, params, LocaleContextHolder.getLocale());
    }

    public static boolean checkNationalCode(String nationalCode) {
        if (isNull(nationalCode) || nationalCode.length() != 10) {
            return false;
        }

        try {

            int controlDigit = NumberUtil.longValue(nationalCode.substring(9, 10)).intValue();

            int sum = 0;
            for (int i = 0; i < 9; i++) {
                int digit = NumberUtil.longValue(nationalCode.substring(i, i + 1)).intValue();
                sum += digit * (10 - i);
            }

            int remainder = sum % 11;

            if (remainder < 2)
                return controlDigit == remainder;
            else
                return controlDigit == (11 - remainder);
        } catch (Exception e) {
            return false;
        }
    }
}
