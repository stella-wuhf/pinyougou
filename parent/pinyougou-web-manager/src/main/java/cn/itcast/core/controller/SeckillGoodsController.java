package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀管理
 */
@RestController
@RequestMapping("seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /*秒杀商品分页查询*/
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody SeckillGoods seckillGoods){
        return seckillGoodsService.search(page,rows,seckillGoods);
    }

    /*秒杀商品状态变更*/
    @RequestMapping("updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try {
            seckillGoodsService.updateStatus(ids,status);
            return new Result(true,"修改状态成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"修改状态失败");
        }
    }
}
