package com.mixiao.emos.wx.config;
import com.mixiao.emos.wx.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j//日志
@RestControllerAdvice//捕获异常
public class ExceptionAdvice {
    @ResponseBody//作用其实是将java对象转为json格式的数据
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//500 处理方法正确执行的前提下,后台返回HTTP响应的状态码为@ResponseStatus指定的状态码
    @ExceptionHandler(Exception.class)//捕获全局异常
    public String exceptionHandler(Exception e){
        log.error("执行异常",e);
        if(e instanceof MethodArgumentNotValidException){//后端验证失败抛出的异常-Method...
            MethodArgumentNotValidException exception= (MethodArgumentNotValidException) e;//转换类型
            return exception.getBindingResult().getFieldError().getDefaultMessage();//将错误信息返回给前台 经过精简的
        }
        else if(e instanceof EmosException){
            EmosException exception= (EmosException) e;
            return exception.getMsg();//取出异常的消息
        }
        else if(e instanceof UnauthorizedException){
            return "你不具备相关权限";
        }
        else{
            return "后端执行异常";
        }
    }
}
