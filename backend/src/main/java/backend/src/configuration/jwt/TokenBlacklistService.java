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

    private static final long DEFAULT_TTL_SECONDS = 86_400;

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token) {
        if (token != null && !token.isBlank()) {
            blacklist.put(token, Instant.now().plusSeconds(DEFAULT_TTL_SECONDS));
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
