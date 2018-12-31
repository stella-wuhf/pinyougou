package cn.itcast.core.listener;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;

public class ItemSearchMangerListener implements MessageListener {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private ItemDao itemDao;
    @Override
    public void onMessage(Message message) {
        ActiveMQMapMessage am = (ActiveMQMapMessage) message;
        try {
            long id = am.getLong("id");
            String dStatus = am.getString("dStatus");
            ItemQuery query=new ItemQuery();
            query.createCriteria().andGoodsIdEqualTo(id);
            List<Item> items = itemDao.selectByExample(query);

            //判断dStatus的值，为1那么新增索引，为0删除索引
            if ("1".equals(dStatus)){
                solrTemplate.saveBeans(items,1000);
            }
            if ("0".equals(dStatus)){
                SolrDataQuery querys=new SimpleQuery();
                Criteria criteria=new Criteria("item_goodsid").is(id);
                querys.addCriteria(criteria);
                solrTemplate.delete(querys);
                solrTemplate.commit();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
