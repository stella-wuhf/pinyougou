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

    /*修改商品状态  审核状态*/
    @RequestMapping("updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            goodsService.updateStatus(ids,status);
            return new Result(true,"修改状态成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"修改状态失败");
        }
    }

    /*删除商品,修改商品审核状态*/
    //该删除并非是物理删除，而是修改tb_goods表的is_delete字段为1 ，称之为“逻辑删除”
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            goodsService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

}
