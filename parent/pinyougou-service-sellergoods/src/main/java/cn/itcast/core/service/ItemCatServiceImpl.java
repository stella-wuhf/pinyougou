package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemCatStDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import cn.itcast.core.pojo.item.ItemCatSt;
import cn.itcast.core.pojo.item.ItemCatStQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品分类管理
 */
@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;
    //通过分类名称查询到分类表中的模板id,保存到缓存库中
    //需要页面一加载就保存到缓存库,所以在findByParentId()方法中执行

    //查询商品分类根据父id
    @Override
    public List<ItemCat> findByParentId(Long parentId) {

        //1.查询mysql数据库中所有分类结果集
        List<ItemCat> itemCatList = findAll();
        //2.将结果集保存到缓存库中(hash类型),k:分类名称 v:模板id
        for (ItemCat itemCat : itemCatList) {
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
        }

        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    //查询一个分类
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    //查询全部分类
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    /**
     * 商家申请
     *
     * @param parentId
     * @return
     */
    @Autowired
    private ItemCatStDao itemCatStDao;

    @Override
    public List<ItemCatSt> findByParentIdst(Long parentId, String name) {
        ItemCatStQuery query = new ItemCatStQuery();
        if (name!=null){
            query.createCriteria().andParentIdEqualTo(parentId).andSellerIdEqualTo(name);
        }
        List<ItemCatSt> itemCatSts = itemCatStDao.selectByExample(query);
        return itemCatSts;
    }

    @Override
    public void add(ItemCatSt itemCatSt) {
        itemCatSt.setStatus("0");
        itemCatStDao.insertSelective(itemCatSt);
    }

    @Override
    public void updateStatusst(Long[] ids, String status) {
        ItemCatSt itemCatSt= new ItemCatSt();
        itemCatSt.setStatus(status);
        for (Long id : ids) {
            itemCatSt.setId(id);
            itemCatStDao.updateByPrimaryKeySelective(itemCatSt);
        }
    }

    @Override
    public List<ItemCatSt> findAllst() {
        return itemCatStDao.selectByExample(null);
    }
}
