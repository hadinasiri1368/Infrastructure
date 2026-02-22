package com.infrastructure.filter;

import com.infrastructure.config.jpa.TenantContext;
import com.infrastructure.config.security.RequestContext;
import com.infrastructure.metric.UriNormalizer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class MetricsFilter extends OncePerRequestFilter {

    private final MeterRegistry meterRegistry;
    private final UriNormalizer uriNormalizer;

    public MetricsFilter(MeterRegistry meterRegistry,
                         UriNormalizer uriNormalizer) {
        this.meterRegistry = meterRegistry;
        this.uriNormalizer = uriNormalizer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.nanoTime();
        Exception exception = null;

        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            exception = ex;
            throw ex;
        } finally {

            long duration = System.nanoTime() - startTime;

            recordMetrics(request, response, duration, exception);
        }
    }

    private void recordMetrics(HttpServletRequest request,
                               HttpServletResponse response,
                               long durationNanos,
                               Exception exception) {

        String method = request.getMethod();
        String uri = uriNormalizer.normalize(request.getRequestURI());
        int status = response.getStatus();

        String statusGroup = (status / 100) + "xx";
        String outcome = resolveOutcome(status);

        Timer.builder("http.server.requests.custom")
                .description("HTTP Server Requests")
                .tags(
                        "method", method,
                        "uri", uri,
                        "status", String.valueOf(status),
                        "status_group", statusGroup,
                        "outcome", outcome,
                        "tenant", TenantContext.getCurrentTenant()
                )
                .publishPercentileHistogram()
                .register(meterRegistry)
                .record(durationNanos, TimeUnit.NANOSECONDS);

        if (exception != null) {
            Counter.builder("http.server.errors.custom")
                    .description("HTTP Server Errors")
                    .tags(
                            "method", method,
                            "uri", uri,
                            "exception", exception.getClass().getSimpleName(),
                            "tenant", TenantContext.getCurrentTenant()
                    )
                    .register(meterRegistry)
                    .increment();
        }
    }

    private String resolveOutcome(int status) {
        if (status >= 200 && status < 300) return "SUCCESS";
        if (status >= 400 && status < 500) return "CLIENT_ERROR";
        if (status >= 500) return "SERVER_ERROR";
        return "OTHER";
    }
}