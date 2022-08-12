package com.mixiao.emos.wx.common.util;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class R extends HashMap<String,Object> {
    public R(){
        put("code", HttpStatus.SC_OK); //http封装的类 = 200状态码
        put("msg","success");
    }
    public R put(String key,Object value){
        super.put(key,value);//自定义Put 做链式调用 就是可以一直调用Put方法 不然只能调用一次
        return this;
    }
    public static R ok(){
        return new R();
    }
    public static R ok(String msg){
        R r=new R();
        r.put("msg",msg);
        return r;
    }
    public static R ok(Map<String,Object> map){
        R r=new R();
        r.putAll(map);
        return r;
    }

    public static R error(int code,String msg){
        R r=new R();
        r.put("code",code);
        r.put("msg",msg);
        return r;
    }
    public static R error(String msg){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,msg);//500常量
    }
    public static R error(){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,"未知异常，请联系管理员");
    }
}