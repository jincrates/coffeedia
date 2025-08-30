package io.coffeedia.infrastructure.cache;

import java.time.Duration;

public interface CacheClient {

    boolean exists(String key);

    String read(String key);

    <T> T read(String key, Class<T> valueType);

    void write(String key, String value, Duration ttl);

    void write(String key, Object value, Duration ttl);

    void delete(String key);
}
