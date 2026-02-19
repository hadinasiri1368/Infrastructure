package com.infrastructure.util;

import com.infrastructure.constants.Consts;
import com.infrastructure.exceptions.BaseException;
import com.infrastructure.exceptions.GeneralExceptionType;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class DateUtil extends DateUtils {
    private static final Pattern PATTERN = Pattern.compile(Consts.PERSIAN_DATE_REGEX);

    public static LocalDate convertToGregorian(String persianDate) throws BaseException {
        if (persianDate == null || persianDate.trim().isEmpty()) {
            throw new BaseException(GeneralExceptionType.DATE_CANNOT_BE_NULL);
        }

        if (!PATTERN.matcher(persianDate).matches()) {
            throw new BaseException(GeneralExceptionType.DATE_VALIDATION_FAILED, new Object[]{persianDate});
        }

        try {
            String[] parts = persianDate.split("/");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            return toGregorian(year, month, day);
        } catch (Exception e) {
            throw new BaseException(GeneralExceptionType.DATE_VALIDATION_FAILED, new Object[]{persianDate});
        }
    }


    private static LocalDate toGregorian(int persianYear, int persianMonth, int persianDay) {
        int[] persianMonthDays = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};
        int persianEpoch = 226894;
        int[] gregorianMonthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        int persianDayOfYear = 0;
        for (int i = 0; i < persianMonth - 1; i++) {
            persianDayOfYear += persianMonthDays[i];
        }
        persianDayOfYear += persianDay;

        if (isPersianLeapYear(persianYear) && persianMonth == 12) {
            persianMonthDays[11] = 30;
        }

        int totalPersianDays = (persianYear - 1) * 365 + (persianYear - 1) / 4 + persianDayOfYear + persianEpoch;

        int gregorianYear = 1;
        while (totalPersianDays > 365) {
            if (isGregorianLeapYear(gregorianYear)) {
                if (totalPersianDays > 366) {
                    totalPersianDays -= 366;
                    gregorianYear++;
                } else {
                    break;
                }
            } else {
                totalPersianDays -= 365;
                gregorianYear++;
            }
        }

        int gregorianMonth = 1;
        for (int i = 0; i < 12; i++) {
            int daysInMonth = gregorianMonthDays[i];
            if (i == 1 && isGregorianLeapYear(gregorianYear)) {
                daysInMonth = 29;
            }
            if (totalPersianDays > daysInMonth) {
                totalPersianDays -= daysInMonth;
                gregorianMonth++;
            } else {
                break;
            }
        }

        int gregorianDay = totalPersianDays;

        return LocalDate.of(gregorianYear, gregorianMonth, gregorianDay);
    }

    private static boolean isPersianLeapYear(int year) {
        int mod = year % 33;
        return mod == 1 || mod == 5 || mod == 9 || mod == 13 || mod == 17 || mod == 22 || mod == 26 || mod == 30;
    }

    private static boolean isGregorianLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public static boolean isValid(String persianDate) {
        try {
            convertToGregorian(persianDate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static int[] toJalali(int gy, int gm, int gd) {
        int[] g_d_m = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int[] j_d_m = {0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};

        if (isGregorianLeapYear(gy)) {
            g_d_m[2] = 29;
        }

        int gy2 = (gm > 2) ? (gy + 1) : gy;
        int days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400);

        for (int i = 1; i < gm; ++i) {
            days += g_d_m[i];
        }
        days += gd;

        int jy = -1595 + (33 * (days / 12053));
        days %= 12053;

        jy += 4 * (days / 1461);
        days %= 1461;

        if (days > 365) {
            jy += (days - 1) / 365;
            days = (days - 1) % 365;
        }

        int jm = 0, jd = 0;
        for (int i = 1; i <= 12; i++) {
            if (days < j_d_m[i]) {
                jm = i;
                jd = days + 1;
                break;
            }
            days -= j_d_m[i];
        }

        return new int[]{jy, jm, jd};
    }

    public static String getTodayJalali() {
        LocalDate gregorianDate = LocalDate.now();
        int[] jalaliDate = toJalali(
                gregorianDate.getYear(),
                gregorianDate.getMonthValue(),
                gregorianDate.getDayOfMonth()
        );
        return String.format("%04d/%02d/%02d", jalaliDate[0], jalaliDate[1], jalaliDate[2]);
    }
}
