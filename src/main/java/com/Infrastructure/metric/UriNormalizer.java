package com.infrastructure.metric;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UriNormalizer {

    private static final Pattern DIGITS_PATTERN = Pattern.compile("/\\d+");

    private static final Pattern UUID_PATTERN =
            Pattern.compile("/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");

    private static final Pattern HEX_PATTERN = Pattern.compile("/[a-f0-9]{24,}");

    public String normalize(String uri) {
        if (uri == null || uri.isEmpty()) {
            return "/unknown";
        }

        String path = uri.split("\\?")[0];

        path = DIGITS_PATTERN.matcher(path).replaceAll("/{id}");

        path = UUID_PATTERN.matcher(path).replaceAll("/{uuid}");

        path = HEX_PATTERN.matcher(path).replaceAll("/{hex}");

        return path;
    }
}