package cn.itcast.core.listener;

import cn.itcast.core.service.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *  发布/订阅模式 消费方
 *  生成静态页面
 */
public class PageListener implements MessageListener{

    @Autowired
    private StaticPageService staticPageService;

    //接收消息,处理消息
    @Override
    public void onMessage(Message message) {
        //强制转换类型
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
        try {
            //从消息中获取id
            String id = atm.getText();
            System.out.println("页面项目接收到的id:" + id);
            //3. 静态化处理
            staticPageService.toStaticPage(Long.parseLong(id));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
