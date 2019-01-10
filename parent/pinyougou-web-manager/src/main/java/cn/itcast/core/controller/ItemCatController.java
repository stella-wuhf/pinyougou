package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatSt;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
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

    /*查询商品分类*/
    @RequestMapping("findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
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

    @RequestMapping("itemCatst")
    public List<ItemCatSt> findAllst(){
        return itemCatService.findAllst();
    }

    @RequestMapping("findByParentIdst")
    public List<ItemCatSt> findByParentIdst(Long parentId) throws Exception {
        return itemCatService.findByParentIdst(parentId,null);
    }

    /*修改商品状态  审核状态*/
    @RequestMapping("updateStatusst")
    public Result updateStatusst(Long[] ids, String status){
        try {
            itemCatService.updateStatusst(ids,status);
            return new Result(true,"修改状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改状态失败");
        }
    }
}
