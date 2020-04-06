package com.github_auth.controller;

import com.alibaba.fastjson.JSON;
import com.github_auth.dto.RequestAccessTockenParam;
import com.github_auth.dto.UserInfo;
import com.github_auth.helper.HttpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.Soundbank;

@Controller
public class AuthController {
    @Autowired
    private HttpHelper httpHelper;
    @Value("${github.client.id}")
    String client_id="";
    @Value("${github.client.secret}")
    String client_secret="";
    @Value("github.redirect.uri")
    String redirect_uri;

    @RequestMapping("/callback")
    @ResponseBody
    public UserInfo callback(@RequestParam("code") String code)
    {
        //1.code参数为github回调callback_uri时，github传递过来的
        System.out.println("请求callback...,code:"+code);

        RequestAccessTockenParam param=new RequestAccessTockenParam();
        param.setClient_id(client_id);
        param.setClient_secret(client_secret);
        param.setCode(code);//传入code参数
        param.setRedirect_url(redirect_uri);
        param.setState("test");

        //获取access token
        String url="https://github.com/login/oauth/access_token";
        String json= JSON.toJSONString(param);
        //2.根据传入的参数（包含code），post请求https://github.com/login/oauth/access_token，获取返回值
        String result= httpHelper.Post(url,json);//access_token=your_access_token&scope=user&token_type=bearer
        System.out.println( "callback result:"+result);

        String[] strs=result.split("&");
        String access_token=strs[0].split("=")[1];//解析access_token

        //3.根据access token,请求https://api.github.com/user获取用户信息
        String url_user="https://api.github.com/user?access_token="+access_token;
        String userInfo=httpHelper.Get(url_user);
        System.out.println("userInfo:"+userInfo);//返回的是一个json字符串

        UserInfo user=JSON.parseObject(userInfo,UserInfo.class);
        return  user;
    }
}
