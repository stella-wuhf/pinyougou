package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JmsTemplate jmsTemplate; //activeMQ
    @Autowired
    private Destination smsDestination; //队列目的地
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserDao userDao;

    //发送验证码
    @Override
    public void sendCode(String phone) {
        //1.生成6位验证码
        String code =  RandomStringUtils.randomNumeric(6);
        //2. 将生成的随机数保存到缓存中,设置存活时间
        redisTemplate.boundValueOps(phone).set(code);
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.DAYS);
        //3. 发消息
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //4.填充数据,发送消息
                MapMessage message = session.createMapMessage();
                message.setString("phone", phone);//手机号
                message.setString("signName", "品优购商城");//签名
                message.setString("templateCode", "SMS_126462276");//模板
                message.setString("templateParam", "{\"number\":\"" + code + "\"}");//验证码
                return message;
            }
        });
    }

    //用户注册
    @Override
    public void add(User user, String smscode) {
        String redisCode = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        //1:判断缓存中的验证码是否失效
        if (null != redisCode){
            //验证码没有失效
            //2. 判断验证码是否正确
            if (redisCode.equals(smscode)){
                //验证码一致
                user.setCreated(new Date());
                user.setUpdated(new Date());
                userDao.insertSelective(user);
            }else {
                //验证码不一致
                 throw  new RuntimeException("验证码错误,请重新输入");
            }
        }else {
            //验证码失效
            throw new RuntimeException("验证码失效!");
        }
    }
}
