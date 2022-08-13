package com.mixiao.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

//@ApiModel
//使用场景：在实体类上边使用，标记类时swagger的解析类
//@ApiModelProperty
//使用场景：使用在被 @ApiModel 注解的模型类的属性上
@ApiModel
@Data
public class TestSayHelloForm {
    //@NotBlank //不能空
    //@Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,15}$",message = "不符合正则表达式")
    @ApiModelProperty("姓名")
    private String name;
}
