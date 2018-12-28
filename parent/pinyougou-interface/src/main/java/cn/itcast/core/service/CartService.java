package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import pojogroup.Cart;

import java.util.List;

public interface CartService {
    Item findItemByItemId(Long itemId);

    List<Cart> findCatrList(List<Cart> cartList);

    void merge(List<Cart> cartList, String name);

    List<Cart> findCartListFromRedis(String name);
}
