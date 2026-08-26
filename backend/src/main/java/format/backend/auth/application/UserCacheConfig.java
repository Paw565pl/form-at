package format.backend.auth.application;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.boot.cache.autoconfigure.CacheManagerCustomizer;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class UserCacheConfig {

    static final String CACHE_NAME = "users";

    @Bean
    CacheManagerCustomizer<CaffeineCacheManager> usersCacheCustomizer() {
        return cacheManager -> cacheManager.registerCustomCache(
                CACHE_NAME,
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofHours(1))
                        .build());
    }
}
