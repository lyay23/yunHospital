package com.neuedu.hisweb.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.User;
import com.neuedu.hisweb.service.AuthSessionService;
import com.neuedu.hisweb.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthSessionServiceImpl implements AuthSessionService {

    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${his.security.session-ttl-seconds:3600}")
    private long sessionTtlSeconds;

    @Value("${his.security.token-prefix:token:}")
    private String tokenPrefix;

    @Value("${his.security.user-token-set-prefix:user:tokens:}")
    private String userTokenSetPrefix;

    @Value("${his.security.user-ver-prefix:user:ver:}")
    private String userVerPrefix;

    public AuthSessionServiceImpl(JwtUtils jwtUtils, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String issueTokenForUser(User user) {
        Long version = getOrInitUserVersion("USER", user.getId());
        String jti = UUID.randomUUID().toString();
        String token = jwtUtils.createTokenWithSession(user, user.getUseType(), version, jti);
        storeToken("USER", user.getId(), token);
        return token;
    }

    @Override
    public String issueTokenForCustomer(Customer customer) {
        Long version = getOrInitUserVersion("CUSTOMER", customer.getId());
        String jti = UUID.randomUUID().toString();
        String token = jwtUtils.createTokenWithSession(customer, null, version, jti);
        storeToken("CUSTOMER", customer.getId(), token);
        return token;
    }

    @Override
    public boolean isTokenActive(String token, DecodedJWT decodedJWT) {
        if (token == null || decodedJWT == null) {
            return false;
        }
        String kind = decodedJWT.getClaim("kind").asString();
        Integer userId = decodedJWT.getClaim("id").asInt();
        Long tokenVer = decodedJWT.getClaim("ver").asLong();

        String tokenKey = tokenPrefix + token;
        Object exists = redisTemplate.opsForValue().get(tokenKey);
        if (exists == null) {
            return false;
        }
        Long currentVer = getOrInitUserVersion(kind, userId);
        return tokenVer != null && tokenVer.equals(currentVer);
    }

    @Override
    public void invalidateToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        redisTemplate.delete(tokenPrefix + token);
        DecodedJWT decodedJWT = jwtUtils.parseToken(token);
        if (decodedJWT != null) {
            String kind = decodedJWT.getClaim("kind").asString();
            Integer userId = decodedJWT.getClaim("id").asInt();
            if (kind != null && userId != null) {
                String setKey = userTokenSetPrefix + kind + ":" + userId;
                redisTemplate.opsForSet().remove(setKey, token);
            }
        }
    }

    @Override
    public void kickUser(String kind, Integer userId) {
        if (userId == null || kind == null) {
            return;
        }
        String setKey = userTokenSetPrefix + kind + ":" + userId;
        Set<Object> tokens = redisTemplate.opsForSet().members(setKey);
        if (tokens != null) {
            for (Object t : tokens) {
                if (t != null) {
                    redisTemplate.delete(tokenPrefix + t);
                }
            }
        }
        redisTemplate.delete(setKey);
        bumpUserVersion(kind, userId);
    }

    @Override
    public void bumpUserVersion(String kind, Integer userId) {
        if (userId == null || kind == null) {
            return;
        }
        String key = userVerPrefix + kind + ":" + userId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    private void storeToken(String kind, Integer userId, String token) {
        String tokenKey = tokenPrefix + token;
        redisTemplate.opsForValue().set(tokenKey, kind + ":" + userId, Duration.ofSeconds(sessionTtlSeconds));
        String setKey = userTokenSetPrefix + kind + ":" + userId;
        redisTemplate.opsForSet().add(setKey, token);
        redisTemplate.expire(setKey, Duration.ofSeconds(sessionTtlSeconds));
    }

    private Long getOrInitUserVersion(String kind, Integer userId) {
        String key = userVerPrefix + kind + ":" + userId;
        Object val = redisTemplate.opsForValue().get(key);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        if (val instanceof String) {
            try {
                return Long.parseLong((String) val);
            } catch (NumberFormatException ignored) {
            }
        }
        redisTemplate.opsForValue().set(key, 1L, Duration.ofDays(30));
        return 1L;
    }
}
