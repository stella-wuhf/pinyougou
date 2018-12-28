package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录管理
 */
@RestController
@RequestMapping("login")
public class LoginController {

    /*获取登录用户信息*/
    @RequestMapping("showName")
    public Map<String,Object> showName(){
        Map<String, Object> map = new HashMap<>();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",name);
        map.put("curTime",new Date());
        return map;
    }
}
