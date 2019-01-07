package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.service.ItemService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 库存管理
 */
@RestController
@RequestMapping("item")
public class ItemController {
    @Reference
    private ItemService itemService;

    /*根据商品id查询全部库存集合*/
    @RequestMapping("findItemListByGoodsId")
    public List<Item> findItemListByGoodsId(Long goodsId){
        List<Item> itemList = itemService.findItemListByGoodsId(goodsId);
        return itemList;
    }

    /*根据库存id查询单个库存*/
    @RequestMapping("findOne")
    public Item findOne(Long itemId){
        return itemService.findOne(itemId);
    }
}
