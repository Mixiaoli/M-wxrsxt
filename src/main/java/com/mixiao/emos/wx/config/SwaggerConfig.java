package com.mixiao.emos.wx.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

//Swagger∴方法都要封装在Docket
@Configuration//注入配置类
@EnableSwagger2//Swagger2 开启
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);//swagger
        ApiInfoBuilder builder = new ApiInfoBuilder();//这个是用来存储信息的 可以直接用
        builder.title("EMOS在线办公系统");
        ApiInfo info = builder.build();//把他封装起来 存到Apiinfo
        docket.apiInfo(info);//存到docket

        //那些页面那些方法需要添加到swagger页面里面
        ApiSelectorBuilder selectorBuilder = docket.select();//查询 什么包里什么类要添加到swagger
        selectorBuilder.paths(PathSelectors.any());//路径所有包 所有的java类
        selectorBuilder.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));//设置特定的注解才能加入swagger页面
        docket = selectorBuilder.build();//然后一样添加到docket里面

        ApiKey apiKey = new ApiKey("token", "token", "header");//来弄JWT单点登陆,请求头里接收客户端上传的令牌 参数是token 提取令牌字符串
        List<ApiKey> apiKeyList = new ArrayList<>();//把apikey封装到list然后添加到docket
        apiKeyList.add(apiKey);
        docket.securitySchemes(apiKeyList);//添加进去
        //令牌的作用域
        AuthorizationScope scope = new AuthorizationScope("global", "accessEverything");//access认证对象为全局
        AuthorizationScope[] scopes = {scope};//封装到数组 swagger封装很麻烦要很多次 封装对象
        SecurityReference reference = new SecurityReference("token", scopes);//token +数组 封装起来
        List refList = new ArrayList();//再次封装起来List
        refList.add(reference);//添加到reflist
        SecurityContext context = SecurityContext.builder().securityReferences(refList).build();//再在封装！到context
        List cxtList = new ArrayList();
        cxtList.add(context);//再次封装到List 这样docket才能用
        docket.securityContexts(cxtList);
        //这样就在Swagger里开启了JWT

        return docket;
    }
}