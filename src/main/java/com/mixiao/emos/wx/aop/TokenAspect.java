package com.mixiao.emos.wx.aop;

import com.mixiao.emos.wx.common.util.R;
import com.mixiao.emos.wx.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect// @Aspect 注解使用详解 AOP为Aspect Oriented Programming的缩写,意为:面向切面编程
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Pointcut("execution(public * com.mixiao.emos.wx.controller.*.*(..))")//声明切点 看拦截哪里 所有的web方法
    public void aspect(){

    }

    @Around("aspect()") //@Around环绕通知 方法之前 之后都可以拦截
    public Object around(ProceedingJoinPoint point) throws Throwable{//通过point可以获得方法执行后的返回值
        R r=(R)point.proceed();//方法执行结果转换为R对象
        String token=threadLocalToken.getToken();
        //如果threadlocaltoken中存在token 说明是新增的token
        if(token!=null){
            r.put("token",token);//往响应中放置token 新令牌
            threadLocalToken.clear();//清理 旧的清理掉
        }
        return r;
    }
}
