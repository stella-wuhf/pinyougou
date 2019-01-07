package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatSt;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  商品分类管理
 */
@RestController
@RequestMapping("itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    /*查询商品分类,根据父id查询全部的子分类*/
    @RequestMapping("findByParentId")
    public List<ItemCatSt> findByParentId(Long parentId){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return itemCatService.findByParentIdst(parentId,name);
    }

    /*查询一个分类*/
    @RequestMapping("findOne")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }

    /*查询全部分类*/
    @RequestMapping("findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }

    /*分类申请*/
    @RequestMapping("add")
    public Result add(@RequestBody ItemCatSt itemCatSt){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        itemCatSt.setSellerId(name);
        try {
            itemCatService.add(itemCatSt);
            return new Result(true,"成功");
        } catch (Exception e) {
            return new Result(false,"失败");
        }
    }
}
