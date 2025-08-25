package dev.roberto.devices.boot;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;

@Component
public class JsonSizeLimitFilter extends OncePerRequestFilter {

  private final long maxBytes;

  public JsonSizeLimitFilter(@Value("${app.http.max-json-bytes:1048576}") long maxBytes) {
    this.maxBytes = maxBytes;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws ServletException, IOException {

    String method = request.getMethod();
    if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
      String ct = request.getContentType();
      if (ct != null && ct.toLowerCase(Locale.ROOT).contains("application/json")) {
        long len = request.getContentLengthLong();
        if (len > 0 && len > maxBytes) {
          response.setStatus(HttpStatus.PAYLOAD_TOO_LARGE.value());
          response.setContentType("application/json");
          String body = """
              {"timestamp":"%s","status":413,"error":"Payload Too Large","message":"Request body exceeds %d bytes","path":"%s"}
              """.formatted(Instant.now(), maxBytes, request.getRequestURI());
          response.getWriter().write(body);
          return;
        }
      }
    }
    chain.doFilter(request, response);
  }
}
