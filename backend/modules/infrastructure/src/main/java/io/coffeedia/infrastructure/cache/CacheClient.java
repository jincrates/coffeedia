package io.coffeedia.infrastructure.cache;

import java.time.Duration;

public interface CacheClient {

    boolean exists(String key);

    String read(String key);

    <T> T read(String key, Class<T> valueType);

    <T> void write(String key, T value, Duration ttl);

    void delete(String key);
}
