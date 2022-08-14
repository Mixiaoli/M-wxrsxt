package com.mixiao.emos.wx.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mixiao.emos.wx.db.dao.TbDeptDao;
import com.mixiao.emos.wx.db.dao.TbUserDao;

import com.mixiao.emos.wx.db.pojo.TbUser;
import com.mixiao.emos.wx.exception.EmosException;
import com.mixiao.emos.wx.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {
    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private TbDeptDao deptDao;
    //临时授权认证 拿openid
    private String getOpenId(String code){
        String url="https://api.weixin.qq.com/sns/jscode2session";
        HashMap map=new HashMap();//往map提交参数
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response=HttpUtil.post(url,map);//发送请求post
        JSONObject json=JSONUtil.parseObj(response);//返回的字符串数据转换json数据
        String openId=json.getStr("openid");//要提取的是opnid
        if(openId==null||openId.length()==0){//openid如果空就是错误
            throw new RuntimeException("临时登陆凭证错误");
        }
        return openId;
    }
    //注册新用户代码 code临时授权
    @Override
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        //如果邀请码是00000 代表超级管理员
        if(registerCode.equals("000000")){
            //查询超级管理员账户是否已经绑定
            boolean bool=userDao.haveRootUser();
            if(!bool){
                //把当前用户绑定到ROOT账户 插入超级管理员
                String openId=getOpenId(code);
                HashMap param=new HashMap();
                param.put("openId", openId);
                param.put("nickname", nickname);
                param.put("photo", photo);
                param.put("role", "[0]");//json数据0就是超级管理员
                param.put("status", 1);
                param.put("createTime", new Date());
                param.put("root", true);
                userDao.insert(param);//插入
                int id=userDao.searchIdByOpenId(openId);//保存主建指
                return id;
            }
            else{
                //如果root已经绑定了 就爆出异常 Emos是业务异常 Runtime的异常是平台的关系 不是业务代码上面的
                throw new EmosException("无法绑定超级管理员账号");
            }
        }
        else{

        }
        return 0;
    }
    //返回角色的权限 都有什么权限
    @Override
    public Set<String> searchUserPermissions(int userId) {
        Set<String> permissions=userDao.searchUserPermissions(userId);
        return permissions;
    }
    @Override
    public Integer login(String code) {
        String openId=getOpenId(code);//根据临时授权字符串
        Integer id=userDao.searchIdByOpenId(openId);//判断是否有这个id 然后看是否能登陆
        if(id==null){
            throw new EmosException("帐户不存在");
        }

        return id;
    }
}
