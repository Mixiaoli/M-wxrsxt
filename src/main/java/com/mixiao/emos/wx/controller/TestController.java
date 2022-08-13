package com.mixiao.emos.wx.controller;

import com.mixiao.emos.wx.common.util.R;
import com.mixiao.emos.wx.controller.form.TestSayHelloForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/test")
@Api("测试Web接口")
public class TestController {
    @PostMapping("/sayHello")
    @ApiOperation("最简单的测试方法")//咋们自己定义的写这个注解就加入Swagger
    //@RequestBody 提交的时候需要打包封装成对象上下文
    public R sayHello(@Valid @RequestBody TestSayHelloForm form) {
        return R.ok().put("message", "Hello,"+form.getName());
    }

    @GetMapping("/hi")
    public R hi(){
        return R.ok();
    }

}