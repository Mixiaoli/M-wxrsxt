package com.mixiao.emos.wx.config.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
//我们要把Realm和Filter配置到shiro里
@Configuration//注入配置类
public class ShiroConfig {

    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm realm){//封装realm类 授权验证
        DefaultWebSecurityManager securityManager=new DefaultWebSecurityManager();//子类default new出来
        securityManager.setRealm(realm);//封装realm对象
        securityManager.setRememberMeManager(null);//传入null 现在服务端并没有保存登陆的凭证 在客户端
        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager,OAuth2Filter filter){//封装过滤类
        ShiroFilterFactoryBean shiroFilter=new ShiroFilterFactoryBean();//子类
        shiroFilter.setSecurityManager(securityManager);
        //过滤
        Map<String , Filter> map=new HashMap<>();
        map.put("oauth2",filter);
        shiroFilter.setFilters(map);//放入
        //看要拦截什么
        Map<String,String> filterMap=new LinkedHashMap<>();
        filterMap.put("/webjars/**", "anon");//不需要拦截 anon
        filterMap.put("/druid/**", "anon");
        filterMap.put("/app/**", "anon");
        filterMap.put("/sys/login", "anon");
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/captcha.jpg", "anon");
        filterMap.put("/user/register", "anon");
        filterMap.put("/user/login", "anon");
        filterMap.put("/test/**", "anon");
        filterMap.put("/meeting/recieveNotify", "anon");
        filterMap.put("/**", "oauth2");//除了上面之外的都要拦截

        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;

    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){//生命周期管理对象
        return new LifecycleBeanPostProcessor();
    }
    //Aop切面类 对象
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor advisor=new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
