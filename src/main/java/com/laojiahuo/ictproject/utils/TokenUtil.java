package com.laojiahuo.ictproject.utils;

import com.laojiahuo.ictproject.VO.UserTokenVO;
import com.laojiahuo.ictproject.PO.UserPO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenUtil {
    /**
     * 过期时间(单位:秒)
     */
    public static final long ACCESS_EXPIRE = 60 * 60 * 24 * 30L; // 单位是秒，30天

    /**
     * 私钥
     * 使用 SecretKey 来生成签名的密钥
     */
//    private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);这个每次都会重新生成
    private static final SecretKey KEY = Keys.hmacShaKeyFor("YourSuperSecretKeyForJwtEncryption123456789012".getBytes());


    /**
     * jwt签发者
     */
    private static final String JWT_ISS = "Tiam";

    /**
     * jwt主题
     */
    private static final String SUBJECT = "Peripherals";

    /**
     * 创建Token
     * @param userPO 用户信息
     * @return JWT token
     */
    public static String createToken(UserPO userPO) {
        // 令牌ID
        String uuid = UUID.randomUUID().toString();
        // 过期日期
        Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_EXPIRE * 1000);
        // 自定义有效载荷
        Map<String, Object> claims = new HashMap<>();
        claims.put("userCode", userPO.getUserCode());
//        claims.put("username", userPO.getUsername());
//        claims.put("password", userPO.getPassword());
        claims.put("email", userPO.getEmail());

        return Jwts.builder()
                .setClaims(claims) // 设置自定义负载信息
                .setId(uuid) // 令牌ID
                .setExpiration(expirationDate) // 过期时间
                .setIssuedAt(new Date()) // 签发时间
                .setSubject(SUBJECT) // 主题
                .setIssuer(JWT_ISS) // 签发者
                .signWith(KEY, SignatureAlgorithm.HS256) // 签名算法和签名密钥
                .compact(); // 生成最终的JWT
    }

    /**
     * 从Token中解析出UserPO对象
     * @param token JWT token
     * @return UserPO 用户对象
     */
    public static UserTokenVO getUserFromToken(String token) {
        // 解析出Token中的Claims负载信息
        Claims claims = parsePayload(token);

        // 提取用户信息
        String userCode = (String) claims.get("userCode");
        String email = (String) claims.get("email");

        // 构造UserPO对象
        UserTokenVO userTokenVO = new UserTokenVO();
        userTokenVO.setUserCode(userCode);
        userTokenVO.setEmail(email);

        return userTokenVO;
    }

    /**
     * 解析token
     * @param token token
     * @return Jws<Claims>
     */
    public static Jws<Claims> parseClaim(String token) {
        // 使用签名密钥解析Token
        return Jwts.parser()
                .setSigningKey(KEY) // 设置用于验证的签名密钥
                .build()
                .parseClaimsJws(token); // 解析token
    }

    /**
     * 解析Token的头部信息
     * @param token JWT token
     * @return JwsHeader
     */
    public static JwsHeader parseHeader(String token) {
        return parseClaim(token).getHeader();
    }

    /**
     * 解析Token的负载信息
     * @param token JWT token
     * @return Claims
     */
    public static Claims parsePayload(String token) {
        return parseClaim(token).getBody();
    }
}
