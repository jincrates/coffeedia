package io.coffeedia.infrastructure.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.coffeedia.infrastructure.cache.CacheClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class RedisClient implements CacheClient {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public boolean exists(final String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Redis exists operation failed for key: {}", key, e);
            return false;
        }
    }

    @Override
    public String read(final String key) {
        return read(key, String.class);
    }

    @Override
    public <T> T read(final String key, final Class<T> valueType) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }

            // 타입이 이미 일치하는 경우
            if (valueType.isInstance(value)) {
                return valueType.cast(value);
            }

            return objectMapper.convertValue(value, valueType);
        } catch (Exception e) {
            log.error("Redis read operation failed for key: {} with type: {}", key,
                valueType.getSimpleName(), e);
            return null;
        }
    }

    @Override
    public <T> void write(final String key, final T value, final Duration ttl) {
        try {
            if (ttl != null && !ttl.isNegative() && !ttl.isZero()) {
                redisTemplate.opsForValue().set(key, value, ttl);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            log.debug("Successfully wrote object to Redis - key: {}, ttl: {}", key, ttl);
        } catch (Exception e) {
            log.error("Redis write operation failed for key: {}", key, e);
        }
    }

    @Override
    public void delete(final String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Successfully deleted from Redis - key: {}", key);
        } catch (Exception e) {
            log.error("Redis delete operation failed for key: {}", key, e);
        }
    }
}
