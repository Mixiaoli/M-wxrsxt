package com.mixiao.emos.wx.config.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
//过滤器在请求里拦截下来的对象去做转义
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) {//子类定义构造器传入接收的请求对象然后
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value= super.getParameter(name);//从请求里获得的原始数据
        if(!StrUtil.hasEmpty(value)){//判断是否为空 !null 就是非空 向下执行
            value= HtmlUtil.filter(value);//Hutool里的方法做转义 把那些代码符号去掉什么的脚本标签去掉符号
        }
        return value;
    }

    @Override//@override注解去直接重写方法
    public String[] getParameterValues(String name) {
        String[] values= super.getParameterValues(name);
        if(values!=null){
            for (int i=0;i<values.length;i++){//便利数组
                String value=values[i];
                if(!StrUtil.hasEmpty(value)){
                    value=HtmlUtil.filter(value);//判断有效就转义
                }
                values[i]=value;//转义后再塞回数组里
            }
        }
        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters = super.getParameterMap();//获取
        LinkedHashMap<String, String[]> map=new LinkedHashMap();//存到有序的linkehashmap里面
        if(parameters!=null){
            for (String key:parameters.keySet()){//获得所有的Key
                String[] values=parameters.get(key);//通过Key获得values
                for (int i = 0; i < values.length; i++) {//处理数组里面的元素
                    String value = values[i];
                    if (!StrUtil.hasEmpty(value)) {
                        value = HtmlUtil.filter(value);
                    }
                    values[i] = value;
                }
                map.put(key,values);//塞回map里面
            }
        }
        return map;
    }

    @Override
    public String getHeader(String name) {//从请求头取到的数据也要做转义
        String value= super.getHeader(name);
        if (!StrUtil.hasEmpty(value)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    //getInputStream方法得到输入流其实就是从服务器端发回的数据。
    @Override
    public ServletInputStream getInputStream() throws IOException {//方法返回值是IO流
        InputStream in= super.getInputStream();
        InputStreamReader reader=new InputStreamReader(in,Charset.forName("UTF-8"));//读取配置
        BufferedReader buffer=new BufferedReader(reader);//缓存流
        StringBuffer body=new StringBuffer();//要做字符串拼接
        String line=buffer.readLine();//读取第一行的数据
        while(line!=null){
            body.append(line);//添加
            line=buffer.readLine();//继续循环向下读取 一行
        }
        buffer.close();//关闭io流
        reader.close();
        in.close();
        Map<String,Object> map= JSONUtil.parseObj(body.toString());//要将Json数据转换成map对象
        Map<String,Object> result=new LinkedHashMap<>();//有序
        for(String key:map.keySet()){
            Object val=map.get(key);
            if(val instanceof String){
                if(!StrUtil.hasEmpty(val.toString())){
                    result.put(key,HtmlUtil.filter(val.toString()));//转义
                }
            }
            else {
                result.put(key,val);//如果不是string类型 不需要转义
            }
        }
        String json=JSONUtil.toJsonStr(result);//拿数据
        ByteArrayInputStream bain=new ByteArrayInputStream(json.getBytes());//io流
        return new ServletInputStream() {//匿名内部类
            @Override
            public int read() throws IOException {//覆盖read方法
                return bain.read();//返回
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
