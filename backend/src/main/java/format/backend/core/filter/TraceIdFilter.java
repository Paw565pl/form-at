package format.backend.core.filter;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
class TraceIdFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        val traceId = getTraceId();
        if (traceId != null) {
            response.setHeader("X-Trace-Id", traceId);
        }

        filterChain.doFilter(request, response);
    }

    private @Nullable String getTraceId() {
        val context = tracer.currentTraceContext().context();
        return context != null ? context.traceId() : null;
    }
}
