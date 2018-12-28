package cn.itcast.core.listener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 发布/订阅模式 消费方
 * 删除索引库
 */
public class ItemDeleteListener implements MessageListener{

    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public void onMessage(Message message) {
        //强制转换类型
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
        try {
            //从消息中获取id
            String id = atm.getText();
            System.out.println("删除商品--搜索项目接收到的id:"+id);
            //删除索引库
            Criteria criteria=new Criteria("item_goodsid").is(id);
            SolrDataQuery query=new SimpleQuery(criteria);
            solrTemplate.delete(query);
            solrTemplate.commit();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
