package backend.src.configuration.jwt;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Returns a JSON 403 response when an authenticated user tries to access
 * a resource they are not allowed to (e.g. editing someone else's team).
 */
@Component
public class AccessDeniedHandlerJwt implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerJwt.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        logger.warn("Access denied for {} to {}", request.getUserPrincipal() != null
                        ? request.getUserPrincipal().getName() : "anonymous", request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        final String body = String.format(
                "{\"status\":%d,\"error\":\"Forbidden\",\"message\":\"%s\",\"path\":\"%s\"}",
                HttpServletResponse.SC_FORBIDDEN,
                "You do not have permission to perform this action",
                escapeJson(request.getServletPath())
        );

        response.getWriter().write(body);
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
