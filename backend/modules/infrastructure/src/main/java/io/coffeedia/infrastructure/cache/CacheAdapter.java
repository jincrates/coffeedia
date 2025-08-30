package io.coffeedia.infrastructure.cache;

import io.coffeedia.application.port.cache.CachePort;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CacheAdapter implements CachePort {

    private final CacheClient cacheClient;

    @Override
    public boolean exists(final String key) {
        return cacheClient.exists(key);
    }

    @Override
    public String read(final String key) {
        return cacheClient.read(key);
    }

    @Override
    public <T> T read(final String key, final Class<T> valueType) {
        return cacheClient.read(key, valueType);
    }

    @Override
    public void write(final String key, final String value, final Duration ttl) {
        cacheClient.write(key, value, ttl);
    }

    @Override
    public void write(final String key, final Object value, final Duration ttl) {
        cacheClient.write(key, value, ttl);
    }

    @Override
    public void delete(final String key) {
        cacheClient.delete(key);
    }
}
