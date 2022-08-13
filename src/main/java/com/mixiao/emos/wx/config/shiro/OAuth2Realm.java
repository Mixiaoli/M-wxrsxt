package com.mixiao.emos.wx.config.shiro;


import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component//交给spring
public class OAuth2Realm extends AuthorizingRealm {//认证与授权shiro

    @Autowired
    private JwtUtil jwtUtil;//变量 要覆盖认证方法 要处理令牌字符串 所以要用JwtUtil

    /*@Autowired
    private UserService userService;*/

    @Override
    public boolean supports(AuthenticationToken token) {//被覆盖的方法 传入令牌对象 不是令牌字符串 封装好的
        return token instanceof OAuth2Token;//同类型就可以开始
    }

    /**
     * 授权(验证权限时调用) 覆盖
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection collection) {

        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        //查询用户的权限列表
        //吧权限列表添加到info对象中
        return info;
    }

    /**
     * 认证(验证登录时调用) 覆盖
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //从令牌获取userid,然后检测该用户是否被冻结
        SimpleAuthenticationInfo info=new SimpleAuthenticationInfo();
        //往Info对象中添加用户信息，token字符串
        return info;
    }
}
