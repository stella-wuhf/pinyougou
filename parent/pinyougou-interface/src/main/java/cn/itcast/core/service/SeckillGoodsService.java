package cn.itcast.core.service;

import cn.itcast.core.pojo.seckill.SeckillGoods;
import entity.PageResult;

public interface SeckillGoodsService {
    void add(SeckillGoods seckillGoods);

    PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods);

    void updateStatus(Long[] ids, String status);
}
