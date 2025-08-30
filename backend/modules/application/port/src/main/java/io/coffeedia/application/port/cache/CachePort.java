package io.coffeedia.application.port.cache;

import java.time.Duration;

public interface CachePort {

    boolean exists(String key);

    String read(String key);

    <T> T read(String key, Class<T> valueType);

    <T> void write(String key, T value, Duration ttl);

    void delete(String key);
}
