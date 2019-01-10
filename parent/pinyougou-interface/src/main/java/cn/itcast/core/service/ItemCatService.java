package cn.itcast.core.service;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatSt;

import java.util.List;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long parentId);

    ItemCat findOne(Long id);

    List<ItemCat> findAll();


    /**
     * 商家申请
     * @param parentId
     * @return
     */
    List<ItemCatSt> findByParentIdst(Long parentId, String name);

    void add(ItemCatSt itemCatSt);

    List<ItemCatSt> findAllst();

    void updateStatusst(Long[] ids, String status);
}
