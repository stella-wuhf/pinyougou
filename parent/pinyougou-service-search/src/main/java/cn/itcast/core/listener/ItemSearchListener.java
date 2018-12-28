package cn.itcast.core.listener;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;

/**
 * 发布/订阅模式 消费方
 *  审核通过后,将数据导入solr索引库
 */
public class ItemSearchListener implements MessageListener {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private ItemDao itemDao;

   //接收消息,处理消息
    @Override
    public void onMessage(Message message) {
        //强制转换类型
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
        try {
            //从消息中获取id
            String id = atm.getText();
            System.out.println("搜索项目接收到的id:"+id);
            //2. 保存商品信息到索引库,需要保存的是库存表集合
            //根据商品id,作为外键,查库存对象集合
            ItemQuery itemQuery=new ItemQuery();
            //只要默认显示的,有库存的
            itemQuery.createCriteria().andGoodsIdEqualTo(Long.parseLong(id)).andIsDefaultEqualTo("1").andStatusEqualTo("1");
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            //将库存对象集合 保存到索引库中
            solrTemplate.saveBeans(itemList,1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
