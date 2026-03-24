package com.neuedu.hisweb.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类，用于生成和验证JSON Web Token
 *
 * JWT由三部分组成：
 * 1. Header: 包含令牌类型和签名算法
 * 2. Payload: 包含用户信息和元数据
 * 3. Signature: 用于验证令牌的完整性
 *
 * 格式: Header.Payload.Signature
 */
@Component
public class JwtUtils {

    // 签名密钥，用于生成和验证JWT签名
    public static final String SECRET = "SECRET";

    // 应用级别的密钥，用于额外的安全验证
    private String secretkey;

    // JWT过期时间（秒），从配置文件注入，默认1年
    private Long expireTime;

    // 从配置文件中注入应用密钥
    @Value("${jwt.secretkey}")
    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    // 从配置文件中注入JWT过期时间，默认值为1年（31536000秒）
    @Value("${jwt.expireTime:31536000}")
    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 根据用户对象生成JWT令牌
     *
     * @param object 用户对象，可以是User或Customer类型
     * @return 生成的JWT令牌
     */
    public String createToken(Object object) {
        return createTokenWithSession(object, object instanceof User ? ((User) object).getUseType() : null, 1L, String.valueOf(System.currentTimeMillis()));
    }

    public String createTokenWithSession(Object object, Integer userType, Long version, String tokenId) {
        Date expireDate = new Date(System.currentTimeMillis() + expireTime * 1000);
        JWTCreator.Builder builder = JWT.create()
                .withClaim("secretkey", secretkey)
                .withClaim("ver", version)
                .withClaim("jti", tokenId)
                .withExpiresAt(expireDate);

        if (object instanceof Customer) {
            Customer customer = (Customer) object;
            return builder.withClaim("id", customer.getId())
                    .withClaim("kind", "CUSTOMER")
                    .sign(Algorithm.HMAC256(SECRET));
        } else if (object instanceof User) {
            User user = (User) object;
            return builder.withClaim("realName", user.getRealName())
                    .withClaim("userName", user.getUserName())
                    .withClaim("id", user.getId())
                    .withClaim("userType", userType)
                    .withClaim("kind", "USER")
                    .sign(Algorithm.HMAC256(SECRET));
        }
        throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
    }

    /**
     * 验证JWT令牌的有效性
     *
     * @param token 待验证的JWT令牌
     * @return 验证结果，true表示有效，false表示无效
     */
    public boolean verify(String token) {
        try {
            // 创建JWT验证器，使用相同的密钥和算法
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            // 验证令牌，如果验证失败会抛出异常
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            // 捕获验证异常，返回验证失败
            return false;
        }
    }

    /**
     * 从JWT令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名，如果令牌无效则返回null
     */
    public String getUserNameByToken(String token) {
        try {
            // 解码JWT令牌，获取声明信息
            DecodedJWT decodedJWT = JWT.decode(token);
            // 获取userName声明
            return decodedJWT.getClaim("userName").asString();
        } catch (JWTDecodeException e) {
            // 处理解码异常，返回null表示获取失败
            return null;
        }
    }

    /**
     * 从JWT令牌中获取用户对象
     *
     * @param token JWT令牌
     * @return 用户对象（User或Customer），如果令牌无效则返回null
     */
    public Object getUserByToken(String token) {
        try {
            // 解码JWT令牌，获取声明信息
            DecodedJWT decodedJWT = JWT.decode(token);

            // 根据是否存在userName声明判断用户类型
            if (decodedJWT.getClaim("userName").isNull()) {
                // 没有userName声明，创建Customer对象
                Customer customer = new Customer();
                customer.setId(decodedJWT.getClaim("id").asInt());
                return customer;
            }

            // 有userName声明，创建User对象
            User user = new User();
            user.setUserName(decodedJWT.getClaim("userName").asString());
            user.setRealName(decodedJWT.getClaim("realName").asString());
            user.setId(decodedJWT.getClaim("id").asInt());
            user.setUseType(decodedJWT.getClaim("userType").asInt());
            return user;
        } catch (JWTDecodeException e) {
            // 处理解码异常，返回null表示获取失败
            return null;
        }
    }

    public DecodedJWT parseToken(String token) {
        try {
            return JWT.decode(token);
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 验证JWT令牌并返回解码后的JWT对象
     *
     * @param token JWT令牌
     * @return 解码后的JWT对象
     */
    private DecodedJWT verifyAndGetJWT(String token) {
        // 创建验证器并验证令牌
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        return verifier.verify(token);
    }
}
