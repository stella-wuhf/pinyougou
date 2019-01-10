package cn.itcast.core.service;


import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;

@Service
@Transactional
public class IndexServiceImpl implements IndexService{
    @Autowired
    private OrderDao orderDao;
    @Autowired
    RedisTemplate redisTemplate;

    public HashMap<String, Object> findindexCatList() {
        HashMap<String, Object> map = new HashMap<>();
        int i = orderDao.countByExampleGroupNum();
        OrderQuery orderQuery = new OrderQuery();
        orderQuery.createCriteria().andCreateTimeEqualToMonth();
        int i1 = orderDao.countByExample(orderQuery);
        map.put("newOderNum",i);
        map.put("monthOrderNum",i1);
        return map;
    }


}
