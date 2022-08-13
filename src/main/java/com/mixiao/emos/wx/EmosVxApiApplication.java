package com.mixiao.emos.wx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan //扫描全部包 不然我们写的过滤器不能用 Servlet、Filter、Listener 可以直接通过 @WebServlet、@WebFilter、@WebListener 注解自动注册
public class EmosVxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmosVxApiApplication.class, args);
    }

}
