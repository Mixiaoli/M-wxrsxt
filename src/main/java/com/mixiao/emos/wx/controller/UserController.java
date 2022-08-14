package com.mixiao.emos.wx.controller;
import cn.hutool.json.JSONUtil;
import com.mixiao.emos.wx.common.util.R;
import com.mixiao.emos.wx.config.shiro.JwtUtil;
import com.mixiao.emos.wx.controller.form.*;
import com.mixiao.emos.wx.exception.EmosException;
import com.mixiao.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@RestController
@RequestMapping("/user")
@Api("用户模块Web接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;


    @PostMapping("/register")
    @ApiOperation("注册用户")
    public R register(@Valid @RequestBody RegisterForm form){
        //将注册码 授权这些传进去
        int id=userService.registerUser(form.getRegisterCode(),form.getCode(),form.getNickname(),form.getPhoto());
        String token=jwtUtil.createToken(id);//创建令牌字符串
        Set<String> permsSet=userService.searchUserPermissions(id);//查询用户权限
        saveCacheToken(token,id);
        return R.ok("用户注册成功").put("token",token).put("permission",permsSet);//返回给客户端数据和权限列表
    }

    @PostMapping("/login")
    @ApiOperation("登陆系统")
    public R login(@Valid @RequestBody LoginForm form){
        int id=userService.login(form.getCode());//登陆 要拿临时授权
        String token=jwtUtil.createToken(id);//生成Token字符串
        saveCacheToken(token,id);//缓存
        Set<String> permsSet = userService.searchUserPermissions(id);//查询用户的权限列表
        return R.ok("登陆成功").put("token",token).put("permission",permsSet);//返回结果给客户端
    }

    //往redis缓存token返回令牌字符串 不如封装成方法
    private void saveCacheToken(String token,int userId){
        redisTemplate.opsForValue().set(token,userId+"",cacheExpire, TimeUnit.DAYS);
    }
}
