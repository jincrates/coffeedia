package io.coffeedia.application.port.cache;

import java.time.Duration;
import java.util.Optional;

public interface CachePort {

    boolean exists(String key);

    Optional<String> read(String key);

    <T> T read(String key, Class<T> valueType);

    void write(String key, String value, Duration ttl);

    void write(String key, Object value, Duration ttl);

    void delete(String key);
}
