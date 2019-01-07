package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Goods;
import entity.PageResult;
import pojogroup.GoodsVo;

import java.util.List;

public interface GoodsService {
    void add(GoodsVo vo);

    PageResult search(Integer page, Integer rows, Goods goods);

    GoodsVo findOne(Long id);

    void update(GoodsVo vo);

    void updateStatus(Long[] ids, String status);

    void delete(Long[] ids);

    /**
     * 商家上架下架管理
     * @param ids
     * @param dStatus
     * @param name
     */
    void updateDelStatus(Long[] ids, String dStatus, String name);

    List<Goods> findGoodsListBySellerId(String sellerId);
}
