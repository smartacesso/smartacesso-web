package br.com.startjob.acesso.entrypoint.api.security;

import br.com.startjob.acesso.core.config.SmartAcessoProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final int capacity;
    private final long windowMillis;

    public LoginRateLimitFilter(SmartAcessoProperties properties) {
        this.capacity = properties.security().loginRateLimit().capacity();
        Duration window = properties.security().loginRateLimit().window();
        this.windowMillis = window != null ? window.toMillis() : 60_000L;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.contains("/restful-services/login/do")
                || path.contains("/restful-services/login/interno")
                || path.contains("/restful-services/app/login")
                || path.contains("/restful-services/pedestre/login")
                || path.contains("/restful-services/responsible/login"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        prune();
        String key = clientKey(request);
        Window window = windows.computeIfAbsent(key, k -> new Window(System.currentTimeMillis()));
        int current = window.count.incrementAndGet();
        if (current > capacity) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many login attempts\",\"code\":\"RATE_LIMITED\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        String ip = forwarded != null && !forwarded.isBlank() ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
        return ip + "|" + request.getRequestURI();
    }

    private void prune() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Window>> it = windows.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Window> entry = it.next();
            if (now - entry.getValue().startedAt > windowMillis) {
                it.remove();
            }
        }
    }

    private static final class Window {
        private final long startedAt;
        private final AtomicInteger count = new AtomicInteger();

        private Window(long startedAt) {
            this.startedAt = startedAt;
        }
    }
}
