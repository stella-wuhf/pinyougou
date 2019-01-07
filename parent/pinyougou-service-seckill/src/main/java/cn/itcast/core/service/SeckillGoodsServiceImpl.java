package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 秒杀管理
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private ItemDao itemDao;
    //商家 秒杀商品申请
    @Override
    public void add(SeckillGoods seckillGoods) {
        //封装数据  已经有的数据 sellerId,goodsId,itemId,title,price,costPrice,introduction,num,
        Item item = itemDao.selectByPrimaryKey(seckillGoods.getItemId());
         //商品图片
        seckillGoods.setSmallPic(item.getImage());
        //添加日期
        seckillGoods.setCreateTime(new Date());
        //审核状态
        seckillGoods.setStatus("0");
        //开始时间
        //结束时间
        //审核日期
        //剩余库存数,应该和秒杀数量一致
        seckillGoods.setStockCount(seckillGoods.getNum());
        //添加
        seckillGoodsDao.insertSelective(seckillGoods);
    }

    //分页查询秒杀商品,状态为0的
    @Override
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {
        //分页插件
        PageHelper.startPage(page, rows);

        //准备条件
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = seckillGoodsQuery.createCriteria();

        //判断商品状态
        if (null != seckillGoods.getStatus() && !"".equals(seckillGoods.getStatus())) {
            criteria.andStatusEqualTo(seckillGoods.getStatus());
        }

        //判断商家id是否存在,如果存在,是商家在查询商品,或者是运营商在有条件查询商品
        if (null != seckillGoods.getSellerId()&& !"".equals(seckillGoods.getStatus())) {
            criteria.andSellerIdEqualTo(seckillGoods.getSellerId());
        }

        Page<SeckillGoods> p = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
        return new PageResult(p.getTotal(), p.getResult());
    }

    //修改秒杀商品状态  审核状态
    @Override
    public void updateStatus(Long[] ids, String status) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setStatus(status);
        for (Long id : ids) {
            seckillGoods.setId(id);
            //设置审核日期
            seckillGoods.setCheckTime(new Date());
            seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);
        }
    }
}
