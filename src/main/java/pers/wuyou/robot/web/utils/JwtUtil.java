package pers.wuyou.robot.web.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.AccountInfo;
import pers.wuyou.robot.common.GlobalVariable;
import pers.wuyou.robot.constants.Constant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyou
 */
@Component
@ConfigurationProperties(prefix = Constant.JWT)
public class JwtUtil {

    private static String secret;
    private static final String ISSUER = "wuyou";
    private static final Integer EXPIRE_TIME = 60 * 60 * 24 * 1000;

    public synchronized void setSecret(String secret) {
        JwtUtil.secret = secret;
    }

    /**
     * 生成token
     *
     * @return token
     */
    public static String sign(String code) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withHeader(header)
                    .withClaim("code", code)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检验token是否正确
     *
     * @param token token
     * @return 用户信息
     */
    public static AccountInfo verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
            DecodedJWT jwt = verifier.verify(token);
            String code = jwt.getClaim("code").asString();
            return GlobalVariable.getAccountFromMemberIndex(code);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
