package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;

import java.util.List;

public interface ItemService {
    List<Item> findItemListByGoodsId(Long goodsId);

    Item findOne(Long itemId);
}
