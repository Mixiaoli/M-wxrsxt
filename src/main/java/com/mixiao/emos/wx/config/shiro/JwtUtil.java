package com.mixiao.emos.wx.config.shiro;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component//交给spring管理
@Slf4j//日志
public class JwtUtil {
    @Value("${emos.jwt.secret}")
    private String secret;//secret 属性

    @Value("${emos.jwt.expire}")
    private int expire;//时间 都是配置自己设置的

    public String createToken(int userId){
        Date date= DateUtil.offset(new Date(), DateField.DAY_OF_YEAR,5);//调用date 常量 5天 过期时间
        Algorithm algorithm=Algorithm.HMAC256(secret);//生成密钥
        JWTCreator.Builder builder= JWT.create();//创建
        String token=builder.withClaim("userId",userId).withExpiresAt(date).sign(algorithm);//绑定Userid和过期时间+加密算法对象
        return token;//返回令牌
    }

    public int getUserId(String token){
        DecodedJWT jwt=JWT.decode(token);//解码对象获取userid
        int userId=jwt.getClaim("userId").asInt();//获取Userid
        return userId;//通过令牌字符串获得Userid
    }

    public void verifierToken(String token){//验证令牌字符串有效性 是否过期
        Algorithm algorithm=Algorithm.HMAC256(secret);//创建算法对象
        JWTVerifier verifier=JWT.require(algorithm).build();//验证对象
        verifier.verify(token);//验证方法
    }
}
