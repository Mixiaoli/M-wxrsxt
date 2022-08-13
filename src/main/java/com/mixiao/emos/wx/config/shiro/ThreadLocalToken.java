package com.mixiao.emos.wx.config.shiro;

import org.springframework.stereotype.Component;

@Component
public class ThreadLocalToken {
    private ThreadLocal<String> local=new ThreadLocal<>();//线程变量 读写数据提供了set get 方法 然后对他们做封装 String类型

    public void setToken(String token){
        local.set(token);//吧令牌传进来保存起来
    }

    public String getToken(){
        return local.get();//获得绑定的数据 之前存的是字符串
    }

    public void clear(){
        local.remove();//清空
    }
}
