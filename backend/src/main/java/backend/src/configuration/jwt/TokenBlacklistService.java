package backend.src.configuration.jwt;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps a thread-safe in-memory blacklist of revoked JWTs so that
 * logging out invalidates the current session token (US-USER-04).
 * Entries are purged lazily once their expiration passes.
 */
@Service
public class TokenBlacklistService {

    private final JwtProperties jwtProperties;
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public TokenBlacklistService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public void blacklist(String token) {
        if (token != null && !token.isBlank()) {
            long ttlSeconds = Math.max(1, (jwtProperties.getExpiration() + 999) / 1000);
            blacklist.put(token, Instant.now().plusSeconds(ttlSeconds));
        }
    }

    public boolean isBlacklisted(String token) {
        cleanExpired();
        return token != null && blacklist.containsKey(token);
    }

    private void cleanExpired() {
        Instant now = Instant.now();
        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
