package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.GoodsVo;

/**
 *  商家的商品管理
 */
@RestController
@RequestMapping("goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /*添加商品,要操作三张表的添加*/
    @RequestMapping("add")
    public Result add(@RequestBody GoodsVo vo){
        try {
            //获取商家id
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //将商家id保存到包装对象的商品表中
            vo.getGoods().setSellerId(name);

            goodsService.add(vo);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    /*商品分页查询*/
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        //获取商id,商家只能查询自己家的商品,所以要固定商家id
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(name);

        return goodsService.search(page,rows,goods);
    }

    /*查询单个实例*/
    @RequestMapping("findOne")
    public GoodsVo findOne(Long id){
        return goodsService.findOne(id);
    }

    /*更新商品*/
    @RequestMapping("update")
    public Result update(@RequestBody GoodsVo vo){
        try {
            goodsService.update(vo);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

}
