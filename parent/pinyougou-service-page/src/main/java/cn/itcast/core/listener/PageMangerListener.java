package cn.itcast.core.listener;

import cn.itcast.core.service.StaticPageServiceImpl;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class PageMangerListener implements MessageListener {
    @Autowired
    private StaticPageServiceImpl staticPageService;
    @Override
    public void onMessage(Message message) {
        ActiveMQMapMessage am = (ActiveMQMapMessage) message;
        try {
            long id = am.getLong("id");
            String dStatus = am.getString("dStatus");
            if ("1".equals(dStatus)){
                staticPageService.toStaticPage(id);
            }
            if ("0".equals(dStatus)){
                staticPageService.delStaticPage(id);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
