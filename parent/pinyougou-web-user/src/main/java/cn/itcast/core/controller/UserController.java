package cn.itcast.core.controller;

import cn.itcast.common.utils.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Reference
    private UserService userService;

    /*发送验证码*/
    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        try {
            //判断手机号格式是否合法
            if (PhoneFormatCheckUtils.isPhoneLegal(phone)){
                //合法,调用service层,发验证码
                userService.sendCode(phone);
                return new Result(true,"验证码发送成功!");
            }else {
                return new Result(false,"手机号格式格式错误！");
            }
        }catch (Exception e){
            //e.printStackTrace();
            return new Result(false,"验证码发送失败!");
        }
    }

    /*用户注册*/
    @RequestMapping("/add")
    public Result add(@RequestBody User user,String smscode ){
        try {
            userService.add(user,smscode);
            return new Result(true, "注册成功");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败");
        }
    }
}
