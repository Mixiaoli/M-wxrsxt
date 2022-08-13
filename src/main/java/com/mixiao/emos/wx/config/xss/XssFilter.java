package com.mixiao.emos.wx.config.xss;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
//过滤器 对所有web资源拦截
@WebFilter(urlPatterns = "/*")//你要拦截的路径的请求是什么 所有都拦截
public class XssFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;//转型成http不然传不了
        XssHttpServletRequestWrapper wrapper=new XssHttpServletRequestWrapper(request);
        filterChain.doFilter(wrapper,servletResponse);
    }

    @Override
    public void destroy() {

    }
}