package com.infrastructure.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    public static String loadSqlFromFile(String path) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(path);
        return new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
    }
}
