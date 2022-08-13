package com.mixiao.emos.wx.config.shiro;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*
* Scope，也称作用域，在 Spring IoC 容器是指其创建的 Bean 对象相对于其他 Bean 对象的请求可见范围。在 Spring IoC 容器中具有以下几种作用域：基本作用域（singleton、prototype），Web 作用域（reqeust、session、globalsession），自定义作用域。
*/
@Component
@Scope("prototype") //作用域 多例对象 一定要多例不然存在local会有问题
public class OAuth2Filter extends AuthenticatingFilter {//继承父类 覆盖方法
    @Autowired
    private ThreadLocalToken threadLocalToken;//媒介类

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;//过期时间 配置里设置的

    @Autowired
    private JwtUtil jwtUtil;//获取令牌这些的类 生成令牌

    @Autowired
    private RedisTemplate redisTemplate;

    //方法覆盖拦截请求 用于令牌字符串封装成令牌字符串
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        //获取请求token 从请求头里 也有可能在请求体
        HttpServletRequest req= (HttpServletRequest) request;
        String token=getRequestToken(req);
        if(StrUtil.isBlank(token)){//判断是否为空
            return null;
        }
        return new OAuth2Token(token);//封装的令牌对象类
    }
    //拦截请求 判断请求是否需要被Shiro处理
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req= (HttpServletRequest) request;
        //Ajax提交application/json数据的时候 会先发送Option请求
        //这里放行OptioNs请求 不需要shiro处理
        if(req.getMethod().equals(RequestMethod.OPTIONS.name())){
            return true;//不处理
        }
        //否者除了Options请求,所有请求都要被shiro处理
        return false;
    }
    //用于需要处理shiro处理的请求
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req= (HttpServletRequest) request;//转换一下
        HttpServletResponse resp= (HttpServletResponse) response;
        resp.setContentType("text/html");//响应头
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");//允许跨域请求 就不需要单独创建一个类
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));//允许跨域请求

        threadLocalToken.clear();//清空 如果判断了要刷新就得清空

        String token=getRequestToken(req);//自定义的把请求头体封装 判断是否有效
        if(StrUtil.isBlank(token)){
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);//状态码 返回 401
            resp.getWriter().print("无效的令牌");//无效
            return false;
        }
        try{
            jwtUtil.verifierToken(token);//是否过期
        }catch (TokenExpiredException e){
            if(redisTemplate.hasKey(token)){//redis里面是否过期 服务器端是否过期
                redisTemplate.delete(token);//删掉
                int userId=jwtUtil.getUserId(token);//从老令牌获得id
                token=jwtUtil.createToken(userId);//然后在重新创建令牌
                redisTemplate.opsForValue().set(token,userId+"",cacheExpire, TimeUnit.DAYS);//redis的方法保存数据
                threadLocalToken.setToken(token);//媒介类也要存一个令牌缓存了数据
            }
            else{
                resp.setStatus(HttpStatus.SC_UNAUTHORIZED);//如果客户端过期了 服务端也没有了
                resp.getWriter().print("令牌已过期");//这时候就需要用户重新登陆
                return false;
            }
        }catch (Exception e){//token内容有问题
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
            resp.getWriter().print("无效的令牌");
            return false;
        }
        boolean bool=executeLogin(request,response);//传入请求和响应间接让shiro执行Realm类 就是授权认证类 判断是否成功失败
        return bool;
    }
    //判断用户没有登陆 或者登陆失败 往客户端返回错误消息
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req= (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        resp.setStatus(HttpStatus.SC_UNAUTHORIZED);//状态码
        try{
            resp.getWriter().print(e.getMessage());//认证失败返回的消息
        }catch (Exception exception){

        }

        return false;
    }
    //这个方法父类继承下来的 掌管拦截请求 事情开始时开启过滤
    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req= (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        super.doFilterInternal(request, response, chain);

    }

    private String getRequestToken(HttpServletRequest request){//从请求头里获得令牌封装成一个方法
        String token=request.getHeader("token");
        if(StrUtil.isBlank(token)){//请求头是空的话
            token=request.getParameter("token");//那就从 请求体里获得数据
        }
        return token;
    }
}
