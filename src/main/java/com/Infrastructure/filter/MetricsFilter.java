package com.infrastructure.filter;

import com.infrastructure.config.security.RequestContext;
import com.infrastructure.metric.UriNormalizer;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MetricsFilter extends OncePerRequestFilter {

    private final MeterRegistry meterRegistry;
    private final UriNormalizer uriNormalizer;

    public MetricsFilter(MeterRegistry meterRegistry, UriNormalizer uriNormalizer) {
        this.meterRegistry = meterRegistry;
        this.uriNormalizer = uriNormalizer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            recordMetrics(request, response, duration, null);
        }
    }

    private void recordMetrics(HttpServletRequest request,
                               HttpServletResponse response,
                               long duration,
                               Exception exception) {

        String method = request.getMethod();
        String uri = uriNormalizer.normalize(request.getRequestURI());
        int status = response.getStatus();

        String username = RequestContext.getUser().getUsername();

        meterRegistry.counter("http.requests.total",
                "method", method,
                "uri", uri,
                "status", String.valueOf(status),
                "status_group", status / 100 + "xx",
                "username", username
        ).increment();

        meterRegistry.timer("http.requests.duration",
                "method", method,
                "uri", uri,
                "status_group", status / 100 + "xx"
        ).record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);

        if (exception != null) {
            meterRegistry.counter("http.requests.errors",
                    "method", method,
                    "uri", uri,
                    "error_type", exception.getClass().getSimpleName()
            ).increment();
        }
    }
}
