//package com.laojiahuo.ictproject.utils;
//
//import com.laojiahuo.ictproject.entity.UserPO;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.CompressionCodecs;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * JwtTokenManager工具类
// */
//@Component
//public class JwtUtils {
//
//    /**
//     * 用于签名的私钥
//     */
//    private final String PRIVATE_KEY = "ictlaojiahuo";
//
//    /**
//     * 签发者
//     */
//    private final String ISSUER = "laojiahuo";
//
//    /**
//     * 过期时间 1 小时
//     */
//    private final long EXPIRATION_ONE_HOUR = 3600L;
//
//    /**
//     * 过期时间 1 月
//     */
//    private final long EXPIRATION_ONE_MONTH = 1000 * 60 * 60 * 24 * 30L;
//
//    /**
//     * jwt生成Token
//     * @param userPO         token存储的 实体类 信息
//     * @param expireTime   token的过期时间
//     * @return
//     */
//    public String createToken(UserPO userPO, long expireTime) {
//        // 过期时间
//        if ( expireTime == 0 ) {
//            // 如果是0，就设置默认 1天 的过期时间
//            expireTime = EXPIRATION_ONE_MONTH;
//        }
//        Map<String, Object> claims = new HashMap<>();
//        // 自定义有效载荷部分, 将User实体类用户名和密码存储
//        claims.put("userCode", userPO.getUserCode());
//        claims.put("userame", userPO.getUsername());
//        claims.put("password", userPO.getPassword());
//
//        String token = Jwts.builder()
//                // 发证人
//                .setIssuer(ISSUER)
//                // 有效载荷
//                .setClaims(claims)
//                // 设定签发时间
//                .setIssuedAt(new Date())
//                // 设置有效时长
//                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
//                // 使用HS512算法签名，PRIVATE_KEY为签名密钥
//                .signWith(SignatureAlgorithm.HS512, PRIVATE_KEY)
//                // compressWith() 压缩方法，当载荷过长时可对其进行压缩
//                // 可采用jjwt实现的两种压缩方法CompressionCodecs.GZIP和CompressionCodecs.DEFLATE
//                .compressWith(CompressionCodecs.GZIP)
//                // 生成JWT
//                .compact();
//        return token;
//    }
//
//    public String createToken(UserPO userPO) {
//        // 设置默认 1 月个 的过期时间
////        long expireTime = EXPIRATION_ONE_MONTH;
//        long expireTime = 1000 * 60 * 60 * 24 * 30L;
//
//        Map<String, Object> claims = new HashMap<>();
//        // 自定义有效载荷部分, 将User实体类用户名和密码存储
//        claims.put("userCode", userPO.getUserCode());
//        claims.put("username", userPO.getUsername());
//        claims.put("password", userPO.getPassword());
//
//        byte[] keyBytes = PRIVATE_KEY.getBytes(StandardCharsets.UTF_8);
//        SecretKey secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
//
//
//        String token = Jwts.builder()
//                // 发证人
//                .setIssuer(ISSUER)
//                // 有效载荷
//                .setClaims(claims)
//                // 设定签发时间
//                .setIssuedAt(new Date())
//                // 设置有效时长
//                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
//                // 使用HS512算法签名，PRIVATE_KEY为签名密钥
//                .signWith(SignatureAlgorithm.HS512, secretKey)
//                // compressWith() 压缩方法，当载荷过长时可对其进行压缩
//                // 可采用jjwt实现的两种压缩方法CompressionCodecs.GZIP和CompressionCodecs.DEFLATE
//                .compressWith(CompressionCodecs.GZIP)
//                // 生成JWT
//                .compact();
//
//        return token;
//    }
//
//    /**
//     * 获取token中的User实体类
//     * @param token
//     * @return
//     */
//    public UserPO getUserFromToken(String token) {
//        // 获取有效载荷
//        Claims claims = getClaimsFromToken(token);
//        if (Objects.isNull(claims)) {
//            throw new RuntimeException("token invalid");
//        }
//        // 解析token后，从有效载荷取出值
//        String userCode = (String) claims.get("userCode");
//        String username = (String) claims.get("username");
//        String password = (String) claims.get("password");
//        // 封装成User实体类
//        UserPO userPO = new UserPO();
//        userPO.setUserCode(userCode);
//        userPO.setUsername( username );
//        userPO.setPassword( password );
//
//        if (userCode == null && userCode.equals("")) {
//            throw new RuntimeException("token过期");
//        }
//
//        return userPO;
//    }
//    /**
//     * 获取有效载荷
//     * @param token
//     * @return
//     */
//    public Claims getClaimsFromToken(String token){
//        Claims claims = null;
//        try {
//            claims = Jwts.parser()
//                    //设定解密私钥
//                    .setSigningKey(PRIVATE_KEY)
//                    //传入Token
//                    .parseClaimsJws(token)
//                    //获取载荷类
//                    .getBody();
//        }catch (Exception e){
//            return null;
//        }
//        return claims;
//    }
//}
