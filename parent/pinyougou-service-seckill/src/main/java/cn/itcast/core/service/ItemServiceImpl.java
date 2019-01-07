package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 库存管理
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDao itemDao;

    //根据商品id查询库存集合
    @Override
    public List<Item> findItemListByGoodsId(Long goodsId) {
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(goodsId);
        return itemDao.selectByExample(itemQuery);
    }

    //查询单个实体
    @Override
    public Item findOne(Long itemId) {
        return itemDao.selectByPrimaryKey(itemId);
    }
}
